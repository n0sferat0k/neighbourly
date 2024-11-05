package com.neighbourly.app.c_business.usecase.content

import com.neighbourly.app.d_entity.data.FileContents
import com.neighbourly.app.d_entity.data.Item
import com.neighbourly.app.d_entity.interf.Api
import com.neighbourly.app.d_entity.interf.Db
import com.neighbourly.app.d_entity.interf.SessionStore

class ItemManagementUseCase(
    val dbInteractor: Db,
    val apiGw: Api,
    val sessionStore: SessionStore,
) {
    suspend fun delete(itemId: Int) {
        val token = sessionStore.user?.authtoken

        token?.let {
            apiGw.deleteItem(token, itemId)
            dbInteractor.deleteItem(itemId)
        }
    }

    suspend fun addOrUpdate(item: Item): Int? {
        val token = sessionStore.user?.authtoken

        return token?.let {
            val updatedItem = apiGw.addOrUpdateItem(token, item)
            dbInteractor.storeItems(listOf(updatedItem))
            updatedItem.id
        }
    }

    suspend fun addImage(itemId: Int, imageFileContents: FileContents) {
        val token = sessionStore.user?.authtoken

        token?.let {
            apiGw.uploadItemImage(it, itemId, imageFileContents).let { img ->
                dbInteractor.getItem(itemId).let {
                    dbInteractor.storeItems(listOf(it.copy(images = it.images + img)))
                }
            }
        }
    }

    suspend fun addFile(itemId: Int, fileContents: FileContents) {
        val token = sessionStore.user?.authtoken

        token?.let {
            apiGw.uploadItemFile(it, itemId, fileContents).let { file ->
                dbInteractor.getItem(itemId).let {
                    dbInteractor.storeItems(listOf(it.copy(files = it.files + file)))
                }
            }
        }
    }

    suspend fun delImage(itemId: Int?, imageId: Int) {
        val token = sessionStore.user?.authtoken
        token?.let {
            apiGw.deleteItemImage(it, imageId)
            itemId?.let {
                dbInteractor.getItem(itemId).let {
                    dbInteractor.storeItems(listOf(it.copy(images = it.images.filter { it.key != imageId })))
                }
            }
        }
    }

    suspend fun delFile(itemId: Int?, fileId: Int) {
        val token = sessionStore.user?.authtoken
        token?.let {
            apiGw.deleteItemFile(it, fileId)
            itemId?.let {
                dbInteractor.getItem(itemId).let {
                    dbInteractor.storeItems(listOf(it.copy(files = it.files.filter { it.key != fileId })))
                }
            }
        }
    }
}