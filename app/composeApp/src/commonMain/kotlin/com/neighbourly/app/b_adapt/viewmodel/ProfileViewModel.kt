package com.neighbourly.app.b_adapt.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darkrockstudios.libraries.mpfilepicker.MPFile
import com.neighbourly.app.d_entity.data.OpException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    val profileUpdateUseCase: ProfileUpdateUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(ProfileViewState())
    val state: StateFlow<ProfileViewState> = _state.asStateFlow()

    fun onProfileImageUpdate(file: MPFile<Any>) {
        viewModelScope.launch {
            try {
                registerUseCase.execute(username, password, fullName, email, phoneNumber)
                _state.update { it.copy(error = "", loading = false) }
            } catch (e: OpException) {
                _state.update { it.copy(error = e.msg, loading = false) }
            }
        }
    }

    data class ProfileViewState(
        val error: String = "",
        val loading: Boolean = false,
    )
}
