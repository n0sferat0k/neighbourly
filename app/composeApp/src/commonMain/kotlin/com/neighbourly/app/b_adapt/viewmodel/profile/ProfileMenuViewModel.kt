package com.neighbourly.app.b_adapt.viewmodel.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neighbourly.app.c_business.usecase.profile.ProfileImageUpdateUseCase
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

class ProfileMenuViewModel(
    val profileImageUpdateUseCase: ProfileImageUpdateUseCase,
    val sessionStore: SessionStore,
) : ViewModel() {
    private val _state = MutableStateFlow(ProfileMenuViewState())
    val state: StateFlow<ProfileMenuViewState> = _state.asStateFlow()

    init {
        sessionStore.userFlow
            .onEach {
                it?.let { user ->
                    _state.update {
                        it.copy(
                            imageurl = user.imageurl,
                            hasHousehold = user.household != null,
                            householdLocalized = user.household?.location != null,
                            hasNeighbourhoods = user.neighbourhoods.isNotEmpty(),
                        )
                    }
                } ?: run {
                    _state.update { ProfileMenuViewState() }
                }
            }.launchIn(viewModelScope)
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

    data class ProfileMenuViewState(
        val error: String = "",
        val imageUpdating: Boolean = false,
        val imageurl: String? = null,
        val hasHousehold: Boolean = false,
        val householdLocalized: Boolean = false,
        val hasNeighbourhoods: Boolean = false,
    )
}
