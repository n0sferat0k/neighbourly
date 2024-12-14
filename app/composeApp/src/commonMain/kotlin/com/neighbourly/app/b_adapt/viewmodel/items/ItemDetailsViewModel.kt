package com.neighbourly.app.b_adapt.viewmodel.items

import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neighbourly.app.c_business.usecase.content.ContentSyncUseCase
import com.neighbourly.app.c_business.usecase.content.ItemManagementUseCase
import com.neighbourly.app.d_entity.data.Item
import com.neighbourly.app.d_entity.data.ItemType
import com.neighbourly.app.d_entity.data.ItemType.NEED
import com.neighbourly.app.d_entity.data.ItemType.REMINDER
import com.neighbourly.app.d_entity.data.ItemType.REQUEST
import com.neighbourly.app.d_entity.data.OpException
import com.neighbourly.app.d_entity.interf.Db
import com.neighbourly.app.d_entity.interf.SessionStore
import com.neighbourly.app.loadContentsFromFile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import java.nio.file.Paths

class ItemDetailsViewModel(
    val database: Db,
    val store: SessionStore,
    val syncItemsUseCase: ContentSyncUseCase,
    val itemManagementUseCase: ItemManagementUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(ItemDetailsViewState())
    val state: StateFlow<ItemDetailsViewState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            database.getUsers().let { users ->
                _state.update {
                    it.copy(users = users.map {
                        it.id to (it.fullname ?: it.username)
                    }.toMap())
                }
            }
        }
    }

    fun setItem(itemId: Int?) {
        _state.update {
            it.copy(
                newImages = emptyList(),
                newFiles = emptyMap(),
                saving = false,
                error = ""
            )
        }
        if (itemId != null) {
            _state.update { it.copy(itemId = itemId) }
            viewModelScope.launch {
                database.getItem(itemId = itemId).let { item ->
                    _state.update {
                        it.copy(
                            editable = item.householdId == store.user?.household?.householdid,
                            admin = store.user?.neighbourhoods?.firstOrNull { it.neighbourhoodid == item.neighbourhoodId }?.access?.let { it >= 499 }
                                ?: false,
                            neighbourhoodId = item.neighbourhoodId,
                            type = item.type.name,
                            name = item.name.orEmpty(),
                            description = item.description.orEmpty(),
                            dates = if (item.type == REMINDER) (
                                    item.description?.split(",")
                                        ?.map { Instant.fromEpochSeconds(it.toLong()) }
                                        ?: emptyList()
                                    ) else emptyList(),
                            url = item.url.orEmpty(),
                            start = item.startTs.takeIf { it > 0 }
                                ?.let { Instant.fromEpochSeconds(it.toLong()) },
                            end = item.endTs.takeIf { it > 0 }
                                ?.let { Instant.fromEpochSeconds(it.toLong()) },
                            images = item.images,
                            targetUserId = item.targetUserId,
                            files = item.files.map {
                                FileVS(
                                    id = it.key,
                                    url = it.value,
                                    name = it.value.split("/").last()
                                )
                            })
                    }
                }
            }
        } else {
            _state.update {
                it.copy(
                    neighbourhoodId = store.user?.neighbourhoods?.firstOrNull()?.neighbourhoodid,
                    itemId = null,
                    editable = true,
                    type = ItemType.DONATION.name,
                    typeOverride = null,
                    name = "",
                    nameOverride = null,
                    description = "",
                    descriptionOverride = null,
                    url = "",
                    urlOverride = null,
                    images = emptyMap(),
                    files = emptyList(),
                    newImages = emptyList(),
                    newFiles = emptyMap(),
                )
            }
        }
    }

    fun updateName(name: String) =
        _state.update { it.copy(nameOverride = name, nameError = name.isBlank()) }

    fun updateDescription(description: String) =
        _state.update { it.copy(descriptionOverride = description) }

    fun updateUrl(url: String) = _state.update { it.copy(urlOverride = url) }

    fun deleteImage(imageId: Int) {
        viewModelScope.launch {
            try {
                _state.update { it.copy(error = "", saving = true) }
                itemManagementUseCase.delImage(_state.value.itemId, imageId)
                _state.update {
                    it.copy(
                        error = "",
                        saving = false,
                        images = it.images.filter { it.key != imageId }.toMap()
                    )
                }
            } catch (e: OpException) {
                _state.update { it.copy(error = e.msg, saving = false) }
            }
        }
    }

    fun deleteFile(fileId: Int) {
        viewModelScope.launch {
            try {
                _state.update { it.copy(error = "", saving = true) }
                itemManagementUseCase.delFile(_state.value.itemId, fileId)
                _state.update {
                    it.copy(
                        error = "",
                        saving = true,
                        files = it.files.filter { it.id != fileId }
                    )
                }
            } catch (e: OpException) {
                _state.update { it.copy(error = e.msg, saving = false) }
            }
        }
    }

    fun deleteItem() {
        viewModelScope.launch {
            try {
                _state.update { it.copy(error = "", saving = true) }
                _state.value.itemId?.let { itemManagementUseCase.delete(it) }
                _state.update { it.copy(error = "", saving = true) }
            } catch (e: OpException) {
                _state.update { it.copy(error = e.msg, saving = false) }
            }
        }
    }

    fun save() {
        viewModelScope.launch {
            if (!_state.value.nameError) {
                _state.value.let {
                    try {
                        _state.update { it.copy(saving = true) }

                        itemManagementUseCase.addOrUpdate(
                            Item(
                                id = it.itemId,
                                type = ItemType.getByName(it.typeOverride ?: it.type),
                                name = it.nameOverride ?: it.name,
                                description = it.descriptionOverride ?: it.description,
                                url = it.urlOverride ?: it.url,
                                targetUserId = it.targetUserIdOverride ?: it.targetUserId ?: -1,
                                startTs = (it.startOverride ?: it.start)?.epochSeconds?.toInt()
                                    ?: 0,
                                endTs = (it.endOverride ?: it.end)?.epochSeconds?.toInt() ?: 0,
                                neighbourhoodId = it.neighbourhoodId,
                            )
                        )?.let { newItemId ->
                            it.newImages.forEach { newImage ->
                                loadContentsFromFile(newImage.name)?.let { fileContent ->
                                    itemManagementUseCase.addImage(newItemId, fileContent)
                                }
                            }
                            it.newFiles.keys.forEach { newFile ->
                                loadContentsFromFile(newFile)?.let { fileContent ->
                                    itemManagementUseCase.addFile(newItemId, fileContent)
                                }
                            }

                            setItem(newItemId)
                        }

                        _state.update { it.copy(saving = false) }

                    } catch (e: OpException) {
                        _state.update { it.copy(error = e.msg, saving = false) }
                    }
                }
            }
        }
    }

    fun setType(type: String) {

        _state.update {
            it.copy(
                typeOverride = type,
                targetUserIdOverride = if (listOf(
                        NEED.name,
                        REQUEST.name
                    ).contains(type)
                ) it.targetUserIdOverride else -1
            )
        }
    }

    fun onAddImage(file: String, img: BitmapPainter) {
        _state.update { it.copy(newImages = it.newImages + MemImg(file, img)) }
    }

    fun onAddFile(file: String) {
        val name = Paths.get(file).fileName.toString()

        _state.update { it.copy(newFiles = it.newFiles + (file to name)) }
    }

    fun deleteNewImage(imgName: String) {
        _state.update { it.copy(newImages = it.newImages.filter { it.name != imgName }.toList()) }
    }

    fun setTargetUser(targetUserId: Int) {
        _state.update { it.copy(targetUserIdOverride = targetUserId) }
    }

    fun updateStartDate(startTs: Int) {
        _state.update {
            it.copy(startOverride = startTs.let { Instant.fromEpochSeconds(it.toLong()) })
        }
    }

    fun updateEndDate(endTs: Int) {
        _state.update {
            it.copy(endOverride = endTs.let { Instant.fromEpochSeconds(it.toLong()) })
        }
    }

    fun addOrUpdateDate(ts: Int) {

    }

    data class ItemDetailsViewState(
        val editable: Boolean = false,
        val admin: Boolean = false,

        val saving: Boolean = false,
        val error: String = "",
        val itemId: Int? = null,
        val neighbourhoodId: Int? = null,

        val images: Map<Int, String> = emptyMap(),
        val newImages: List<MemImg> = emptyList(),

        val targetUserId: Int? = null,
        val targetUserIdOverride: Int? = null,

        val files: List<FileVS> = emptyList(),
        val newFiles: Map<String, String> = emptyMap(),

        val type: String = ItemType.DONATION.name,
        val name: String = "",
        val description: String = "",
        val dates: List<Instant> = emptyList(),
        val url: String = "",
        val start: Instant? = null,
        val end: Instant? = null,

        val typeOverride: String? = null,
        val nameOverride: String? = null,
        val descriptionOverride: String? = null,
        val urlOverride: String? = null,
        val startOverride: Instant? = null,
        val endOverride: Instant? = null,

        val nameError: Boolean = false,

        val users: Map<Int, String> = emptyMap(),
    )

    data class FileVS(val id: Int, val url: String, val name: String)

    data class MemImg(val name: String, val img: Painter)
}
