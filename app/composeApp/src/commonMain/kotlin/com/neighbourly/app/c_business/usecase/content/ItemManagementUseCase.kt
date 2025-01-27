package com.neighbourly.app.c_business.usecase.content

import com.neighbourly.app.d_entity.data.FileContents
import com.neighbourly.app.d_entity.data.Item
import com.neighbourly.app.d_entity.interf.Api
import com.neighbourly.app.d_entity.interf.Db
import com.neighbourly.app.d_entity.interf.SessionStore
import com.neighbourly.app.d_entity.interf.Summonable

class ItemManagementUseCase(
    val database: Db,
    val apiGw: Api,
    val sessionStore: SessionStore,
    val summonable: Summonable,
) {
    suspend fun delete(itemId: Int) {
        val token = sessionStore.user?.authtoken

        token?.let {
            apiGw.deleteItem(token, itemId)
            database.deleteItem(itemId)
            summonable.summonOnItemOp()
        }
    }

    suspend fun addOrUpdate(item: Item): Int? {
        val token = sessionStore.user?.authtoken

        return token?.let {
            val updatedItem = apiGw.addOrUpdateItem(token, item)
            database.storeItems(listOf(updatedItem))
            summonable.summonOnItemOp()
            updatedItem.id
        }
    }

    suspend fun addImage(itemId: Int, imageFileContents: FileContents) {
        val token = sessionStore.user?.authtoken

        token?.let {
            apiGw.uploadItemImage(it, itemId, imageFileContents).let { img ->
                database.getItem(itemId).let {
                    database.storeItems(listOf(it.copy(images = it.images + img)))
                    summonable.summonOnItemOp()
                }
            }
        }
    }

    suspend fun addFile(itemId: Int, fileContents: FileContents) {
        val token = sessionStore.user?.authtoken

        token?.let {
            apiGw.uploadItemFile(it, itemId, fileContents).let { file ->
                database.getItem(itemId).let {
                    database.storeItems(listOf(it.copy(files = it.files + file)))
                    summonable.summonOnItemOp()
                }
            }
        }
    }

    suspend fun delImage(itemId: Int?, imageId: Int) {
        val token = sessionStore.user?.authtoken
        token?.let {
            apiGw.deleteItemImage(it, imageId)
            itemId?.let {
                database.getItem(itemId).let {
                    database.storeItems(listOf(it.copy(images = it.images.filter { it.id != imageId })))
                    summonable.summonOnItemOp()
                }
            }
        }
    }

    suspend fun delFile(itemId: Int?, fileId: Int) {
        val token = sessionStore.user?.authtoken
        token?.let {
            apiGw.deleteItemFile(it, fileId)
            itemId?.let {
                database.getItem(itemId).let {
                    database.storeItems(listOf(it.copy(files = it.files.filter { it.id != fileId })))
                    summonable.summonOnItemOp()
                }
            }
        }
    }
}