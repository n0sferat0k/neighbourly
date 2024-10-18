package com.neighbourly.app.c_business.usecase.content

import com.neighbourly.app.d_entity.interf.Api
import com.neighbourly.app.d_entity.interf.Db
import com.neighbourly.app.d_entity.interf.SessionStore

class ItemManagementUseCase(
    val dbInteractor: Db,
    val apiGw: Api,
    val sessionStore: SessionStore,
) {
    suspend fun delete(itemId:Int) {
        val token = sessionStore.user?.authtoken

        token?.let {
            apiGw.deleteItem(token, itemId)
            dbInteractor.deleteItem(itemId)
        }
    }
}