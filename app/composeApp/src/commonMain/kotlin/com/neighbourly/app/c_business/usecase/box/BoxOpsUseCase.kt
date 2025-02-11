package com.neighbourly.app.c_business.usecase.box

import com.neighbourly.app.d_entity.data.BoxShare
import com.neighbourly.app.d_entity.interf.Api
import com.neighbourly.app.d_entity.interf.SessionStore

class BoxOpsUseCase(
    val apiGw: Api,
    val sessionStore: SessionStore,
) {
    suspend fun unlockBox(boxId: String, unlock: Boolean) {
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

    suspend fun addOrUpdateBox(boxId: String, boxName: String) {
        val token = sessionStore.user?.authtoken

        token?.let {
            apiGw.addOrUpdateBox(token, boxId, boxName)
        }
    }

    suspend fun addSharedBox(boxShareToken: String) {
        val token = sessionStore.user?.authtoken

        token?.let {
            apiGw.addSharedBox(token, boxShareToken)
        }
    }

    suspend fun removeBox(boxId: String) {
        val token = sessionStore.user?.authtoken

        token?.let {
            apiGw.removeBox(token, boxId)
        }
    }

    suspend fun delShareBox(shareId: Int) {
        val token = sessionStore.user?.authtoken

        token?.let {
            apiGw.delShareBox(token, shareId)
        }
    }

    suspend fun getBoxShareToken(boxId: String, shareName: String): BoxShare? {
        val token = sessionStore.user?.authtoken

        return token?.let {
            apiGw.shareBox(token, boxId, shareName)
        }
    }
}