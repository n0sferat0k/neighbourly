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
        sessionStore.user
            .onEach {
                it?.let { user ->
                    _state.update {
                        it.copy(
                            hasHouse = user.household != null,
                            localized = user.household?.location != null,
                            localizing = user.localizing,
                            gpsprogress = user.household?.gpsprogress ?: 0f,
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

    data class HouseholdLocalizeViewState(
        val hasHouse: Boolean = false,
        val localized: Boolean = false,
        val localizing: Boolean = false,
        val gpsprogress: Float = 0f,
    )
}
