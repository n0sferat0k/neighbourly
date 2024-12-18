package com.neighbourly.app.d_entity.interf

interface StatusUpdater {
    fun setOnline(isOnline: Boolean, lastError: String? = null)
}