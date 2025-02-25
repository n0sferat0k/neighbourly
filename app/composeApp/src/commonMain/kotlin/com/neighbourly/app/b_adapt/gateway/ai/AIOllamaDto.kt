package com.neighbourly.app.b_adapt.gateway.ai

import com.neighbourly.app.b_adapt.gateway.ai.bean.constructOverviewSystemPrompt
import kotlinx.serialization.Serializable

@Serializable
data class GenerateInputOllama(
    val model: String = "deepseek-r1:14B",
    val stream: Boolean = false,
    val system: String,
    val prompt: String
)

@Serializable
data class AIResponseOllama(
    val model: String,
    val response: String,
)

fun overviewInputOllama(jsonContext: String, prompt: String) = GenerateInputOllama(
    system = constructOverviewSystemPrompt(jsonContext),
    prompt = prompt
)

