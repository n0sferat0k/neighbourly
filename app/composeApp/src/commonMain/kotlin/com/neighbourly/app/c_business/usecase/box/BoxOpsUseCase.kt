package com.neighbourly.app.c_business.usecase.box

import com.neighbourly.app.d_entity.interf.Api
import com.neighbourly.app.d_entity.interf.SessionStore

class BoxOpsUseCase(
    val apiGw: Api,
    val sessionStore: SessionStore,
) {
    suspend fun unlockBox(boxId: String, unlock:Boolean) {
        val token = sessionStore.user?.authtoken

        token?.let {
            apiGw.unlockBox(token, boxId, unlock)
        }
    }

    suspend fun lightBox(boxId: String, light: Boolean) {
        val token = sessionStore.user?.authtoken

        token?.let {
            apiGw.lightBox(token, boxId, light)
        }
    }

    suspend fun openBox(boxId: String) {
        val token = sessionStore.user?.authtoken

        token?.let {
            apiGw.openBox(token, boxId)
        }
    }

    suspend fun addBox(boxId: String, boxName: String) {
        val token = sessionStore.user?.authtoken

        token?.let {
            apiGw.addBox(token, boxId, boxName)
        }
    }

    suspend fun removeBox(boxId: String) {
        val token = sessionStore.user?.authtoken

        token?.let {
            apiGw.removeBox(token, boxId)
        }
    }
}