package com.neighbourly.app.b_adapt.viewmodel.box

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neighbourly.app.b_adapt.viewmodel.bean.BoxShareVS
import com.neighbourly.app.b_adapt.viewmodel.bean.BoxVS
import com.neighbourly.app.b_adapt.viewmodel.bean.toBoxShareVS
import com.neighbourly.app.b_adapt.viewmodel.bean.toHouseholdVS
import com.neighbourly.app.c_business.usecase.box.BoxOpsUseCase
import com.neighbourly.app.c_business.usecase.profile.ProfileRefreshUseCase
import com.neighbourly.app.d_entity.data.OpException
import com.neighbourly.app.d_entity.interf.Db
import com.neighbourly.app.d_entity.interf.Iot
import com.neighbourly.app.d_entity.interf.SessionStore
import com.neighbourly.app.d_entity.util.isValidMac
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BoxManagementViewModel(
    val sessionStore: SessionStore,
    val database: Db,
    val iot: Iot,
    val boxOpsUseCase: BoxOpsUseCase,
    val profileRefreshUseCase: ProfileRefreshUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(BoxManagementViewState())
    val state: StateFlow<BoxManagementViewState> = _state.asStateFlow()

    init {
        sessionStore.userFlow
            .onEach { user ->
                user?.household?.let { household ->
                    val shareHouses =
                        household.boxes?.map { it.shares.map { it.householdId } }?.flatten()
                            .let { shareHouseIds ->
                                database.filterHouseholds(shareHouseIds)
                            }
                    _state.update {
                        it.copy(
                            boxes = household.boxes?.map { box ->
                                BoxVS(
                                    id = box.id,
                                    name = box.name,
                                    owned = box.householdId == household.householdid,
                                    //keep the online status for known boxes (in rename case)
                                    online = _state.value.boxes.firstOrNull { it.id == box.id }?.online
                                        ?: false,
                                    shares = box.shares.map { share ->
                                        share.toBoxShareVS(
                                            shareHouses.firstOrNull { it.householdid == share.householdId }
                                                ?.toHouseholdVS()
                                        )
                                    }
                                )
                            }.orEmpty()
                        )
                    }
                }
            }.launchIn(viewModelScope)

        iot.boxStateFlow
            .onEach { boxStateUpdate ->
                _state.update {
                    it.copy(boxes = _state.value.boxes.map {
                        if (it.id == boxStateUpdate.id) it.copy(
                            online = boxStateUpdate.online ?: it.online,
                            triggered = boxStateUpdate.triggered ?: it.triggered,
                            unlocked = boxStateUpdate.unlocked ?: it.unlocked,
                            lit = boxStateUpdate.lit ?: it.lit,
                        ) else it
                    })
                }
            }.launchIn(viewModelScope)

        iot.iotConnectedFlow.onEach { online ->
            _state.update {
                it.copy(online = online)
            }
        }.launchIn(viewModelScope)
    }

    fun monitor(doMonitor: Boolean = false, boxIds: List<String> = emptyList()) {
        viewModelScope.launch {
            if (doMonitor) {
                iot.monitorBoxes(boxIds)
            } else {
                iot.monitorBoxes(emptyList())
            }
        }
    }

    fun openBox(boxId: String) {
        viewModelScope.launch {
            _state.update { it.copy(saving = true) }
            try {
                boxOpsUseCase.openBox(boxId)
                _state.update { it.copy(saving = false, error = "") }
            } catch (e: OpException) {
                _state.update { it.copy(saving = false, error = e.msg) }
            }
        }
    }

    fun unlockBox(boxId: String, unlock: Boolean) {
        viewModelScope.launch {
            _state.update { it.copy(saving = true) }
            try {
                boxOpsUseCase.unlockBox(boxId, unlock)
                _state.update { it.copy(saving = false, error = "") }
            } catch (e: OpException) {
                _state.update { it.copy(saving = false, error = e.msg) }
            }
        }
    }

    fun lightBox(boxId: String, light: Boolean) {
        viewModelScope.launch {
            _state.update { it.copy(saving = true) }
            try {
                boxOpsUseCase.lightBox(boxId, light)
                _state.update { it.copy(saving = false, error = "") }
            } catch (e: OpException) {
                _state.update { it.copy(saving = false, error = e.msg) }
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _state.update { it.copy(loading = true) }
            try {
                profileRefreshUseCase.execute()
                _state.update { it.copy(loading = false) }
            } catch (e: OpException) {
                _state.update { it.copy(loading = false, error = e.msg) }
            }
        }
    }

    fun addBox(scanResult: String) {
        if (scanResult.isValidMac()) {
            _state.update { it.copy(newBoxId = scanResult) }
        } else {
            viewModelScope.launch {
                _state.update { it.copy(saving = true, error = "") }
                try {
                    boxOpsUseCase.addSharedBox(scanResult)
                    profileRefreshUseCase.execute()
                    _state.update { it.copy(saving = false, newBoxId = "", boxName = "") }
                } catch (e: OpException) {
                    _state.update { it.copy(saving = false, error = e.msg) }
                }
            }
        }
    }

    fun shareBox(boxId: String) {
        _state.update { it.copy(shareableBoxId = boxId) }
    }

    fun clearBox() {
        _state.update {
            it.copy(
                newBoxId = "",
                shareableBoxId = "",
                boxName = "",
                error = ""
            )
        }
    }

    fun saveBox(name: String) {
        if (!name.isBlank()) {
            viewModelScope.launch {
                _state.update { it.copy(saving = true, error = "") }
                try {
                    boxOpsUseCase.addOrUpdateBox(_state.value.newBoxId, name)
                    profileRefreshUseCase.execute()
                    _state.update { it.copy(saving = false, newBoxId = "", boxName = "") }
                } catch (e: OpException) {
                    _state.update { it.copy(saving = false, error = e.msg) }
                }
            }
        }
    }

    fun saveBoxShare(name: String) {
        if (!name.isBlank()) {
            viewModelScope.launch {
                _state.update { it.copy(saving = true, error = "") }
                try {
                    val shareBox =
                        boxOpsUseCase.getBoxShareToken(_state.value.shareableBoxId, name)
                            ?.toBoxShareVS(null)
                    profileRefreshUseCase.execute()
                    _state.update {
                        it.copy(saving = false, shareableBoxId = "", shareBox = shareBox)
                    }
                } catch (e: OpException) {
                    _state.update { it.copy(saving = false, error = e.msg) }
                }
            }
        }
    }

    fun shareBoxSelect(shareBox: BoxShareVS?) {
        _state.update {
            it.copy(saving = false, shareableBoxId = "", shareBox = shareBox)
        }
    }

    fun shareBoxDelete(shareBox: BoxShareVS) {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = "") }
            try {
                boxOpsUseCase.delShareBox(shareBox.id)
                profileRefreshUseCase.execute()
                _state.update { it.copy(loading = false) }
            } catch (e: OpException) {
                _state.update { it.copy(loading = false, error = e.msg) }
            }
        }
    }

    fun removeBox(boxId: String) {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = "") }
            try {
                boxOpsUseCase.removeBox(boxId)
                profileRefreshUseCase.execute()
                _state.update { it.copy(loading = false) }
            } catch (e: OpException) {
                _state.update { it.copy(loading = false, error = e.msg) }
            }
        }
    }

    fun editBox(boxId: String, boxName: String) {
        if (_state.value.boxes.firstOrNull { it.id == boxId }?.owned == true) {
            _state.update { it.copy(newBoxId = boxId, boxName = boxName) }
        }
    }

    data class BoxManagementViewState(
        val saving: Boolean = false,
        val loading: Boolean = false,
        val newBoxId: String = "",
        val shareableBoxId: String = "",
        val shareBox: BoxShareVS? = null,
        val boxName: String = "",
        val error: String = "",
        val boxes: List<BoxVS> = emptyList(),
        val online: Boolean = false
    )
}