package com.neighbourly.app.b_adapt.viewmodel.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neighbourly.app.c_business.usecase.ai.AiChatUseCase
import com.neighbourly.app.d_entity.interf.ConfigStatusSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AiInterfaceViewModel(
    val configSource: ConfigStatusSource,
    val aiChatUseCase: AiChatUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(AiInterfaceViewState())
    val state: StateFlow<AiInterfaceViewState> = _state.asStateFlow()

    init {
        configSource.isAiOnlineFlow.onEach { isAiOnline ->
            _state.update { it.copy(isAiOnline = isAiOnline) }
        }.launchIn(viewModelScope)

        configSource.aiMessages.onEach { messages ->
            _state.update { it.copy(aiMessages = messages) }
        }.launchIn(viewModelScope)
    }

    fun onPrompt(prompt: String) {
        viewModelScope.launch {
            aiChatUseCase.execute(prompt)
        }
    }

    data class AiInterfaceViewState(

        val isAiOnline: Boolean = false,
        val aiMessages: List<String> = emptyList()
    )
}