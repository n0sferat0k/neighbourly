package com.neighbourly.app.d_entity.data

sealed class AiVariant {
    data class AiVariantOllama(val url: String) : AiVariant()
    data class AiVariantGemini(val apiKey: String) : AiVariant()
}