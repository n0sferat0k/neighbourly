package com.neighbourly.app.b_adapt.viewmodel.box

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neighbourly.app.b_adapt.viewmodel.bean.BoxVS
import com.neighbourly.app.c_business.usecase.box.BoxOpsUseCase
import com.neighbourly.app.c_business.usecase.profile.ProfileRefreshUseCase
import com.neighbourly.app.d_entity.data.OpException
import com.neighbourly.app.d_entity.interf.Iot
import com.neighbourly.app.d_entity.interf.SessionStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BoxManagementViewModel(
    val sessionStore: SessionStore,
    val iotComm: Iot,
    val boxOpsUseCase: BoxOpsUseCase,
    val profileRefreshUseCase: ProfileRefreshUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(BoxManagementViewState())
    val state: StateFlow<BoxManagementViewState> = _state.asStateFlow()

    init {
        sessionStore.userFlow
            .onEach { user ->
                user?.household?.let { household ->
                    _state.update {
                        it.copy(
                            boxes = user.household.boxes?.map {
                                BoxVS(
                                    id = it.id,
                                    name = it.name
                                )
                            }.orEmpty()
                        )
                    }
                }
            }.launchIn(viewModelScope)

        iotComm.messageFlow.onEach { message ->
            _state.value.boxes.firstOrNull { message.topic.contains(it.id) }?.let { box ->
                when {
                    message.topic.contains("status") -> box.copy(online = message.message == "ONLINE")
                    message.topic.contains("triggered") -> box.copy(triggered = message.message == "TRUE")
                    message.topic.contains("locked") -> box.copy(unlocked = message.message == "FALSE")
                    message.topic.contains("lit") -> box.copy(lit = message.message == "TRUE")
                    else -> null
                }?.let { updatedBox ->
                    _state.update {
                        it.copy(boxes = _state.value.boxes.map { if (it.id == updatedBox.id) updatedBox else it })
                    }
                }
            }
        }.launchIn(viewModelScope)
    }


    fun monitor(boxIds: List<String>?) {
        viewModelScope.launch {
            if (boxIds.isNullOrEmpty()) {
                iotComm.requireDisconnect()
                _state.update { it.copy(boxes = it.boxes.map { it.copy(online = null) })}
            } else {
                iotComm.requireConnect()
                val newBoxes = boxIds.filter { !_state.value.monitoringBoxes.contains(it) }
                val obsoleteBoxes = _state.value.monitoringBoxes.filter { !boxIds.contains(it) }

                obsoleteBoxes.forEach {
                    iotComm.unsubscribe("neighbourlybox/$it/status")
                    iotComm.unsubscribe("neighbourlybox/$it/triggered")
                    iotComm.unsubscribe("neighbourlybox/$it/locked")
                    iotComm.unsubscribe("neighbourlybox/$it/lit")
                }

                newBoxes.forEach {
                    iotComm.subscribe("neighbourlybox/$it/status")
                    iotComm.subscribe("neighbourlybox/$it/triggered")
                    iotComm.subscribe("neighbourlybox/$it/locked")
                    iotComm.subscribe("neighbourlybox/$it/lit")
                }
            }

            _state.update { it.copy(monitoringBoxes = boxIds.orEmpty()) }
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

    fun unlockBox(boxId: String, unlock:Boolean) {
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

    fun addBox(boxId: String) {
        _state.update { it.copy(newBoxId = boxId) }
    }

    fun clearBox() {
        _state.update {
            it.copy(
                newBoxId = "",
                newBoxName = "",
                newBoxNameError = true,
                error = ""
            )
        }
    }

    fun saveBox() {
        if (!_state.value.newBoxNameError)
            viewModelScope.launch {
                _state.update { it.copy(saving = true, error = "") }
                try {
                    boxOpsUseCase.addBox(_state.value.newBoxId, _state.value.newBoxName)
                    profileRefreshUseCase.execute()
                    _state.update { it.copy(saving = false, newBoxId = "", newBoxName = "") }
                } catch (e: OpException) {
                    _state.update { it.copy(saving = false, error = e.msg) }
                }
            }
    }

    fun updateName(boxName: String) {
        _state.update { it.copy(newBoxName = boxName, newBoxNameError = boxName.isBlank()) }
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

    data class BoxManagementViewState(
        val saving: Boolean = false,
        val loading: Boolean = false,
        val newBoxId: String = "",
        val newBoxName: String = "",
        val newBoxNameError: Boolean = true,
        val error: String = "",
        val boxes: List<BoxVS> = emptyList(),
        val monitoringBoxes: List<String> = emptyList()
    )
}