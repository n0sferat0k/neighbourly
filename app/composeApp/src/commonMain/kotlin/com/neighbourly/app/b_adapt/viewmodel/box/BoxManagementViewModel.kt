package com.neighbourly.app.b_adapt.viewmodel.box

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neighbourly.app.c_business.usecase.box.BoxOpsUseCase
import com.neighbourly.app.c_business.usecase.profile.ProfileRefreshUseCase
import com.neighbourly.app.d_entity.data.OpException
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
                            boxes = user.household.boxes?.map { it.id to it.name }?.toMap()
                        )
                    }
                }
            }.launchIn(viewModelScope)
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

    fun unlockBox(boxId: String) {
        viewModelScope.launch {
            _state.update { it.copy(saving = true) }
            try {
                boxOpsUseCase.unlockBox(boxId)
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
        _state.update { it.copy(newBoxId = "", newBoxName = "", newBoxNameError = false, error = "") }
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

    data class BoxManagementViewState(
        val saving: Boolean = false,
        val loading: Boolean = false,
        val newBoxId: String = "",
        val newBoxName: String = "",
        val newBoxNameError: Boolean = false,
        val error: String = "",
        val boxes: Map<String, String>? = null
    )
}