package com.neighbourly.app.b_adapt.viewmodel.items

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neighbourly.app.c_business.usecase.content.ContentSyncUseCase
import com.neighbourly.app.c_business.usecase.content.ItemManagementUseCase
import com.neighbourly.app.d_entity.interf.Db
import com.neighbourly.app.d_entity.interf.SessionStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ItemDetailsViewModel(
    val database: Db,
    val store: SessionStore,
    val syncItemsUseCase: ContentSyncUseCase,
    val itemManagementUseCase: ItemManagementUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(ItemDetailsViewState())
    val state: StateFlow<ItemDetailsViewState> = _state.asStateFlow()

    fun setItem(itemId: Int?) {
        _state.update { it.copy(itemId = itemId, editable = (itemId == null)) }
        if (itemId != null) {
            viewModelScope.launch {
                database.getItem(itemId = itemId).let { item ->
                    _state.update {
                        it.copy(
                            name = item.name.orEmpty(),
                            description = item.description.orEmpty(),
                            images = item.images
                        )
                    }
                }
            }
        }
    }

    fun updateName(name: String) =
        _state.update { it.copy(nameOverride = name, nameError = name.isBlank()) }

    fun updateDescription(description: String) =
        _state.update { it.copy(descriptionOverride = description) }

    fun deleteImage(imageId: Int) {

    }

    fun onSave() {

    }

    data class ItemDetailsViewState(
        val itemId: Int? = null,
        val editable: Boolean = false,

        val images: Map<Int, String> = emptyMap(),

        val name: String = "",
        val description: String = "",

        val nameOverride: String? = null,
        val descriptionOverride: String? = null,

        val nameError: Boolean = false,
    )
}
