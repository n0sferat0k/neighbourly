package com.neighbourly.app.b_adapt.viewmodel.items

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neighbourly.app.c_business.usecase.items.ContentSyncUseCase
import com.neighbourly.app.d_entity.data.ItemType
import com.neighbourly.app.d_entity.data.OpException
import com.neighbourly.app.d_entity.interf.Db
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FilteredItemListViewModel(
    val database: Db,
    val syncItemsUseCase: ContentSyncUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(FilteredItemListViewState())
    val state: StateFlow<FilteredItemListViewState> = _state.asStateFlow()

    fun setType(type: ItemType) {
        _state.update { it.copy(type = type) }
        refresh()
    }

    fun refresh(force:Boolean = false) {
        viewModelScope.launch {
            _state.update { it.copy(loading = true) }
            try {
                syncItemsUseCase.execute(force)
                database.filterItemsByType(_state.value.type).let { items ->
                    _state.update {
                        it.copy(
                            loading = false,
                            items = items.map {
                                ItemVS(
                                    id = it.id,
                                    name = it.name.orEmpty()
                                )
                            })
                    }
                }
            } catch (e: OpException) {
                _state.update { it.copy(loading = false) }
            }
        }
    }

    data class FilteredItemListViewState(
        val type: ItemType = ItemType.INFO,
        val loading: Boolean = false,
        val items: List<ItemVS> = emptyList()
    )

    data class ItemVS(val id: Int, val name: String)
}