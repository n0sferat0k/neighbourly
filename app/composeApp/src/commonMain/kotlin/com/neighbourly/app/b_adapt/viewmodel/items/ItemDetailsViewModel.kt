package com.neighbourly.app.b_adapt.viewmodel.items

import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neighbourly.app.b_adapt.viewmodel.bean.AttachmentVS
import com.neighbourly.app.b_adapt.viewmodel.bean.ItemVS
import com.neighbourly.app.b_adapt.viewmodel.bean.MemImgVS
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
import com.neighbourly.app.d_entity.util.isValidUrl
import com.neighbourly.app.loadContentsFromFile
import com.neighbourly.app.loadNameFromFile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.Instant.Companion.fromEpochSeconds

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
                deleted = false,
                name = "",
                description = "",
                url = "",
                images = emptyList(),
                files = emptyList(),
                newImages = emptyList(),
                newFiles = emptyMap(),
                start = null,
                end = null,
                saving = false,
                hasChanged = false,
                error = "",
                nameError = false,
                targetUserIdOverride = null,
                typeOverride = null,
                nameOverride = null,
                descriptionOverride = null,
                urlOverride = null,
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
                            dates = if (item.type == REMINDER) {
                                kotlin.runCatching {
                                    item.description?.split(",")
                                        ?.map { fromEpochSeconds(it.toLong()) }
                                        ?: emptyList()
                                }.getOrNull() ?: emptyList()
                            } else emptyList(),
                            url = item.url.orEmpty(),
                            start = item.startTs.takeIf { it > 0 }
                                ?.let { fromEpochSeconds(it.toLong()) },
                            end = item.endTs.takeIf { it > 0 }
                                ?.let { fromEpochSeconds(it.toLong()) },
                            images = item.images.map { AttachmentVS(it.id, it.url, it.name) },
                            targetUserId = item.targetUserId,
                            files = item.files.map {
                                AttachmentVS(
                                    id = it.id,
                                    url = it.url,
                                    name = it.name,
                                )
                            },
                        )
                    }
                }
            }
        } else {
            _state.update {
                it.copy(
                    neighbourhoodId = store.user?.neighbourhoods?.firstOrNull()?.neighbourhoodid,
                    itemId = null,
                    editable = true,
                    admin = store.user?.neighbourhoods?.firstOrNull()?.access?.let { it >= 499 }
                        ?: false,
                    type = ItemType.DONATION.name,
                )
            }
        }
    }

    fun updateName(name: String) =
        _state.update {
            it.copy(
                nameOverride = name,
                nameError = name.isBlank(),
                hasChanged = true
            )
        }

    fun updateUrl(url: String) = _state.update {
        it.copy(
            urlOverride = url,
            urlError = !(url.isBlank() || url.isValidUrl()),
            hasChanged = true
        )
    }

    fun deleteImage(imageId: Int) {
        viewModelScope.launch {
            try {
                _state.update { it.copy(error = "", saving = true) }
                itemManagementUseCase.delImage(_state.value.itemId, imageId)
                _state.update {
                    it.copy(
                        error = "",
                        saving = false,
                        images = it.images.filter { it.id != imageId },
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
                _state.update { it.copy(error = "", saving = false, deleted = true) }
            } catch (e: OpException) {
                _state.update { it.copy(error = e.msg, saving = false) }
            }
        }
    }



    fun onAddImage(file: String, img: BitmapPainter) {
        _state.update { it.copy(newImages = it.newImages + MemImgVS(file, img), hasChanged = true) }
    }

    fun onAddFile(file: String) {
        val name = loadNameFromFile(file)
        _state.update { it.copy(newFiles = it.newFiles + (file to name), hasChanged = true) }
    }

    fun deleteNewImage(imgName: String) {
        _state.update { it.copy(newImages = it.newImages.filter { it.name != imgName }.toList()) }
    }

    fun addOrUpdateDate(ts: Int?, index: Int) {
        if (index == -1 && ts != null) {
            _state.update {
                it.copy(
                    dates = (it.dates + fromEpochSeconds(ts.toLong())).sorted(),
                    hasChanged = true
                )
            }
        }
        if (index > -1 && ts == null) {
            _state.update {
                it.copy(
                    dates = it.dates.toMutableList().apply { removeAt(index) },
                    hasChanged = true
                )
            }
        }
        if (index > -1 && ts != null) {
            _state.update {
                it.copy(
                    dates = it.dates.toMutableList()
                        .apply { this[index] = fromEpochSeconds(ts.toLong()) }
                        .sorted(),
                    hasChanged = true
                )
            }
        }
    }

    fun deleteItemAck() {
        _state.update { it.copy(deleted = false) }
    }

    fun save() {
        viewModelScope.launch {
            if (!_state.value.nameError && !_state.value.urlError && !_state.value.saving) {
                _state.value.let {
                    try {
                        _state.update { it.copy(saving = true) }
                        val type = ItemType.getByName(it.typeOverride ?: it.type)
                        val item = when (type) {
                            REMINDER -> Item(
                                id = it.itemId,
                                type = type,
                                name = it.nameOverride ?: it.name,
                                description = it.dates.map { it.epochSeconds.toString() }
                                    .joinToString(","),
                                url = "",
                                neighbourhoodId = it.neighbourhoodId,
                            )

                            else -> Item(
                                id = it.itemId,
                                type = type,
                                name = it.nameOverride ?: it.name,
                                description = if (type == REMINDER)
                                    it.dates.map { it.epochSeconds.toString() }.joinToString(",")
                                else
                                    it.descriptionOverride ?: it.description,
                                url = it.urlOverride ?: it.url,
                                targetUserId = it.targetUserIdOverride ?: it.targetUserId ?: -1,
                                startTs = (it.startOverride ?: it.start)?.epochSeconds?.toInt()
                                    ?: 0,
                                endTs = (it.endOverride ?: it.end)?.epochSeconds?.toInt() ?: 0,
                                neighbourhoodId = it.neighbourhoodId,
                            )
                        }

                        itemManagementUseCase.addOrUpdate(
                            item
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

                        _state.update { it.copy(saving = false, hasChanged = false) }

                    } catch (e: OpException) {
                        _state.update { it.copy(error = e.msg, saving = false) }
                    }
                }
            }
        }
    }

    data class ItemDetailsViewState(
        val deleted: Boolean = false,
        val editable: Boolean = false,
        val admin: Boolean = false,
        val item: ItemVS = ItemVS(),

        val hasChanged: Boolean = false,
        val saving: Boolean = false,
        val error: String = "",

        val neighbourhoodId: Int? = null,

        val newImages: List<MemImgVS> = emptyList(),



        val newFiles: Map<String, String> = emptyMap(),

        val dates: List<Instant> = emptyList(),

        val urlOverride: String? = null,

        val nameError: Boolean = false,
        val urlError: Boolean = false,

        val users: Map<Int, String> = emptyMap(),
    )
}
