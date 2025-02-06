package com.neighbourly.app.d_entity.interf

interface StatusUpdater {
    fun setOnline(isOnline: Boolean, isTokenExp: Boolean, lastError: String? = null)
    fun setAiOnline(isAiOnline: Boolean)
    fun storeAiMessage(message: String)
}