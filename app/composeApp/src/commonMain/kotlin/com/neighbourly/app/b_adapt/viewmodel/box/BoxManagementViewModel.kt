package com.neighbourly.app.b_adapt.viewmodel.box

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neighbourly.app.c_business.usecase.box.BoxOpsUseCase
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
            _state.update { it.copy(loading = true) }
            try {
                boxOpsUseCase.openBox(boxId)
                _state.update { it.copy(loading = false, error = "") }
            } catch (e: OpException) {
                _state.update { it.copy(loading = false, error = e.msg) }
            }
        }
    }

    fun unlockBox(boxId: String) {
        viewModelScope.launch {
            _state.update { it.copy(loading = true) }
            try {
                boxOpsUseCase.unlockBox(boxId)
                _state.update { it.copy(loading = false, error = "") }
            } catch (e: OpException) {
                _state.update { it.copy(loading = false, error = e.msg) }
            }
        }
    }

    fun refresh() {

    }

    fun addBox(scanString: String? = null) {
        if(scanString  == null) {
            _state.update { it.copy(adding = true) }
        } else {
            _state.update { it.copy(adding = false, loading = true) }

            _state.update { it.copy(loading = false) }
        }
    }

    data class BoxManagementViewState(
        val loading: Boolean = false,
        val error: String = "",
        val adding: Boolean = false,
        val boxes: Map<String, String>? = null
    )
}