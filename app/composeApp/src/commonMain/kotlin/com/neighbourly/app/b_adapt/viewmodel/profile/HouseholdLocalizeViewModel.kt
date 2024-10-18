package com.neighbourly.app.b_adapt.viewmodel.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neighbourly.app.c_business.usecase.profile.HouseholdLocalizeUseCase
import com.neighbourly.app.d_entity.interf.SessionStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HouseholdLocalizeViewModel(
    val sessionStore: SessionStore,
    val householdLocalizeUseCase: HouseholdLocalizeUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(HouseholdLocalizeViewState())
    val state: StateFlow<HouseholdLocalizeViewState> = _state.asStateFlow()

    init {
        sessionStore.userFlow
            .onEach {
                it?.let { user ->
                    _state.update {
                        it.copy(
                            hasHouse = user.household != null,
                            localized = user.household?.location != null,
                            localizing = user.localizing,
                            gpsprogress = user.household?.gpsprogress ?: 0f,
                            editableHousehold = user.household?.headid == user.id,
                        )
                    }
                } ?: run {
                    _state.update { HouseholdLocalizeViewState() }
                }
            }.launchIn(viewModelScope)
    }

    fun onLocalize() {
        viewModelScope.launch {
            householdLocalizeUseCase.startMonitoring()
        }
    }

    fun onAccept() {
        viewModelScope.launch {
            _state.update { it.copy(loading = true) }
            householdLocalizeUseCase.acceptGpsCandidate()
            _state.update { it.copy(loading = false) }
        }
    }

    fun onRetry() {
        viewModelScope.launch {
            _state.update { it.copy(loading = true) }
            householdLocalizeUseCase.retryMonitoring()
            _state.update { it.copy(loading = false) }
        }
    }

    fun onRelocate() {
        viewModelScope.launch {
            _state.update { it.copy(loading = true) }
            householdLocalizeUseCase.relocateHousehold()
            _state.update { it.copy(loading = false) }
        }
    }

    data class HouseholdLocalizeViewState(
        val loading: Boolean = false,
        val hasHouse: Boolean = false,
        val localized: Boolean = false,
        val localizing: Boolean = false,
        val editableHousehold: Boolean = false,
        val gpsprogress: Float = 0f,
    )
}
