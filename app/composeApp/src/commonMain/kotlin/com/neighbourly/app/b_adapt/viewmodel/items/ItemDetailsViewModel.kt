package com.neighbourly.app.b_adapt.viewmodel.items

import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neighbourly.app.c_business.usecase.content.ContentSyncUseCase
import com.neighbourly.app.c_business.usecase.content.ItemManagementUseCase
import com.neighbourly.app.d_entity.data.ItemType
import com.neighbourly.app.d_entity.interf.Db
import com.neighbourly.app.d_entity.interf.SessionStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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

        if (itemId != null) {
            _state.update { it.copy(itemId = itemId) }
            viewModelScope.launch {
                database.getItem(itemId = itemId).let { item ->
                    _state.update {
                        it.copy(
                            type = item.type.name,
                            name = item.name.orEmpty(),
                            description = item.description.orEmpty(),
                            url = item.url.orEmpty(),
                            images = item.images,
                            files = item.files.map {
                                FileVS(
                                    id = it.key,
                                    url = it.value,
                                    name = Paths.get(it.value).fileName.toString()
                                )
                            }
                        )
                    }
                }
            }
        } else {
            _state.update {
                it.copy(
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

    fun updateUrl(url: String) =
        _state.update { it.copy(urlOverride = url) }

    fun deleteImage(imageId: Int) {

    }

    fun onSave() {

    }

    fun setType(type: String) {
        _state.update { it.copy(typeOverride = type) }
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

    fun setTargetUser(it: Int) {

    }

    data class ItemDetailsViewState(
        val saving: Boolean = false,
        val itemId: Int? = null,
        val editable: Boolean = false,

        val images: Map<Int, String> = emptyMap(),
        val newImages: List<MemImg> = emptyList(),

        val files: List<FileVS> = emptyList(),
        val newFiles: Map<String, String> = emptyMap(),

        val type: String = ItemType.DONATION.name,
        val name: String = "",
        val description: String = "",
        val url: String = "",

        val typeOverride: String? = null,
        val nameOverride: String? = null,
        val descriptionOverride: String? = null,
        val urlOverride: String? = null,

        val nameError: Boolean = false,

        val users: Map<Int, String> = emptyMap(),
    )

    data class FileVS(val id: Int, val url: String, val name: String)

    data class MemImg(val name: String, val img: Painter)
}
