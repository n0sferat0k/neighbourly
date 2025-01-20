package com.neighbourly.app.b_adapt.viewmodel.items

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neighbourly.app.b_adapt.viewmodel.bean.AttachmentVS
import com.neighbourly.app.b_adapt.viewmodel.bean.ItemTypeVS
import com.neighbourly.app.b_adapt.viewmodel.bean.ItemVS
import com.neighbourly.app.b_adapt.viewmodel.bean.MemImgVS
import com.neighbourly.app.b_adapt.viewmodel.bean.toItemType
import com.neighbourly.app.b_adapt.viewmodel.bean.toItemTypeVS
import com.neighbourly.app.c_business.usecase.content.ItemManagementUseCase
import com.neighbourly.app.d_entity.data.Item
import com.neighbourly.app.d_entity.data.ItemType.REMINDER
import com.neighbourly.app.d_entity.data.OpException
import com.neighbourly.app.d_entity.interf.Db
import com.neighbourly.app.d_entity.interf.SessionStore
import com.neighbourly.app.d_entity.util.isValidUrl
import com.neighbourly.app.loadContentsFromFile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.Instant.Companion.fromEpochSeconds

class ItemDetailsViewModel(
    private val database: Db,
    private val store: SessionStore,
    private val itemManagementUseCase: ItemManagementUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(ItemDetailsViewState())
    val state: StateFlow<ItemDetailsViewState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            database.getUsers().let { users ->
                _state.update { state ->
                    state.copy(users = users.associate {
                        it.id to (it.fullname ?: it.username)
                    })
                }
            }
        }
    }

    fun reset() {
        _state.update {
            it.copy(
                item = ItemVS(type = ItemTypeVS.DONATION),
                saving = false,
                hasChanged = false,
                error = "",
            )
        }
    }

    fun setItem(itemId: Int?) {
        reset()

        if (itemId != null) {
            _state.update { it.copy(item = it.item?.copy(id = itemId)) }
            viewModelScope.launch {
                database.getItem(itemId = itemId).let { item ->
                    _state.update { state ->
                        state.copy(
                            editable = item.householdId == store.user?.household?.householdid,
                            admin = store.user?.neighbourhoods?.firstOrNull { it.neighbourhoodid == item.neighbourhoodId }?.access?.let { it >= 499 }
                                ?: false,
                            item = state.item?.copy(
                                type = item.type.toItemTypeVS(),
                                name = item.name.orEmpty(),
                                description = item.description.orEmpty(),
                                dates = if (item.type == REMINDER) {
                                    kotlin.runCatching {
                                        item.description?.split(",")
                                            ?.map { fromEpochSeconds(it.toLong()) }
                                            ?: emptyList()
                                    }.getOrNull() ?: emptyList()
                                } else emptyList(),
                                targetUserId = item.targetUserId,
                                url = item.url.orEmpty(),
                                start = item.startTs.takeIf { it > 0 }
                                    ?.let { fromEpochSeconds(it.toLong()) },
                                end = item.endTs.takeIf { it > 0 }
                                    ?.let { fromEpochSeconds(it.toLong()) },
                                images = item.images.map {
                                    AttachmentVS(
                                        it.id ?: 0,
                                        it.url,
                                        it.name
                                    )
                                },
                                files = item.files.map {
                                    AttachmentVS(
                                        id = it.id ?: 0,
                                        url = it.url,
                                        name = it.name,
                                    )
                                },
                                neighbourhoodId = item.neighbourhoodId,
                            ),
                        )
                    }
                }
            }
        } else {
            _state.update { state ->
                state.copy(
                    item = state.item?.copy(neighbourhoodId = store.user?.neighbourhoods?.firstOrNull()?.neighbourhoodid),
                    editable = true,
                    admin = store.user?.neighbourhoods?.firstOrNull()?.access?.let { it >= 499 }
                        ?: false,
                )
            }
        }
    }

    fun deleteImage(imageId: Int) {
        viewModelScope.launch {
            try {
                _state.update { it.copy(error = "", saving = true) }
                itemManagementUseCase.delImage(_state.value.item?.id, imageId)
                _state.update {
                    it.copy(
                        error = "",
                        saving = false,
                        item = it.item?.copy(images = it.item.images.filter { it.id != imageId }),
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
                itemManagementUseCase.delFile(_state.value.item?.id, fileId)
                _state.update {
                    it.copy(
                        error = "",
                        saving = false,
                        item = it.item?.copy(files = it.item.files.filter { it.id != fileId }),
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
                _state.value.item?.id?.let { itemManagementUseCase.delete(it) }
                _state.update { it.copy(error = "", saving = false, item = null) }
            } catch (e: OpException) {
                _state.update { it.copy(error = e.msg, saving = false) }
            }
        }
    }

    fun save(
        typeOverride: ItemTypeVS?,
        nameOverride: String?,
        descriptionOverride: String?,
        datesOverride: List<Instant>?,
        targetUserIdOverride: Int?,
        urlOverride: String?,
        startOverride: Instant?,
        endOverride: Instant?,
        newImages: List<MemImgVS>,
        newFiles: Map<String, String>,
    ) {
        viewModelScope.launch {
            _state.value.item?.let { item ->
                val nameError = (nameOverride ?: item.name).isBlank()
                val urlError = (urlOverride ?: item.url).let { !it.isBlank() && !it.isValidUrl() }
                if (!nameError && !urlError && !_state.value.saving) {
                    try {
                        _state.update { it.copy(saving = true) }
                        val type = (typeOverride ?: item.type).toItemType()
                        val updateItem = when (type) {
                            REMINDER -> Item(
                                id = item.id,
                                type = type,
                                name = nameOverride ?: item.name,
                                description = (datesOverride
                                    ?: item.dates).map { it.epochSeconds.toString() }
                                    .joinToString(","),
                                url = "",
                                neighbourhoodId = item.neighbourhoodId,
                            )

                            else -> Item(
                                id = item.id,
                                type = type,
                                name = nameOverride ?: item.name,
                                description = descriptionOverride ?: item.description,
                                url = urlOverride ?: item.url,
                                targetUserId = targetUserIdOverride ?: item.targetUserId ?: -1,
                                startTs = (startOverride ?: item.start)?.epochSeconds?.toInt()
                                    ?: 0,
                                endTs = (endOverride ?: item.end)?.epochSeconds?.toInt() ?: 0,
                                neighbourhoodId = item.neighbourhoodId,
                            )
                        }

                        itemManagementUseCase.addOrUpdate(
                            updateItem
                        )?.let { newItemId ->
                            newImages.forEach { newImage ->
                                loadContentsFromFile(newImage.name)?.let { fileContent ->
                                    itemManagementUseCase.addImage(newItemId, fileContent)
                                }
                            }
                            newFiles.keys.forEach { newFile ->
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
        val editable: Boolean = false,
        val admin: Boolean = false,
        val item: ItemVS? = ItemVS(type = ItemTypeVS.DONATION),

        val users: Map<Int, String> = emptyMap(),

        val saving: Boolean = false,
        val error: String = "",

        val hasChanged: Boolean = false,
    )
}
