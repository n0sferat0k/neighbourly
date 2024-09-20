package com.neighbourly.app.b_adapt.viewmodel.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neighbourly.app.c_business.usecase.profile.ProfileImageUpdateUseCase
import com.neighbourly.app.c_business.usecase.profile.ProfileRefreshUseCase
import com.neighbourly.app.d_entity.data.FileContents
import com.neighbourly.app.d_entity.data.OpException
import com.neighbourly.app.d_entity.interf.SessionStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    val profileImageUpdateUseCase: ProfileImageUpdateUseCase,
    val profileRefreshUseCase: ProfileRefreshUseCase,
    val sessionStore: SessionStore,
) : ViewModel() {
    private val _state = MutableStateFlow(ProfileViewState())
    val state: StateFlow<ProfileViewState> = _state.asStateFlow()

    init {
        sessionStore.localization
            .onEach { localization ->
                _state.update { it.copy(defaultContentIndex = if (localization.drawing) 3 else 0) }
            }.launchIn(viewModelScope)
    }

    fun refresh() {
        _state.update { it.copy(loading = true) }
        viewModelScope.launch {
            runCatching {
                profileRefreshUseCase.execute()
            }
            _state.update { it.copy(loading = false) }
        }
    }

    fun onProfileImageUpdate(fileContents: FileContents?) {
        viewModelScope.launch {
            try {
                fileContents?.let {
                    _state.update { it.copy(error = "", imageUpdating = true) }
                    profileImageUpdateUseCase.execute(it)
                    _state.update { it.copy(error = "", imageUpdating = false) }
                } ?: run {
                    _state.update { it.copy(error = "Unable to read file", imageUpdating = false) }
                }
            } catch (e: OpException) {
                _state.update { it.copy(error = e.msg, imageUpdating = false) }
            }
        }
    }

    data class ProfileViewState(
        val error: String = "",
        val loading: Boolean = false,
        val imageUpdating: Boolean = false,
        val defaultContentIndex: Int = 0,
    )
}
