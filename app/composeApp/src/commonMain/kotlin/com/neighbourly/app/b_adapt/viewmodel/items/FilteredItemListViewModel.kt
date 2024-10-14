package com.neighbourly.app.b_adapt.viewmodel.items

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neighbourly.app.b_adapt.viewmodel.items.FilteredItemListViewModel.ItemTypeVS.BARTER
import com.neighbourly.app.b_adapt.viewmodel.items.FilteredItemListViewModel.ItemTypeVS.DONATION
import com.neighbourly.app.b_adapt.viewmodel.items.FilteredItemListViewModel.ItemTypeVS.EVENT
import com.neighbourly.app.b_adapt.viewmodel.items.FilteredItemListViewModel.ItemTypeVS.INFO
import com.neighbourly.app.b_adapt.viewmodel.items.FilteredItemListViewModel.ItemTypeVS.NEED
import com.neighbourly.app.b_adapt.viewmodel.items.FilteredItemListViewModel.ItemTypeVS.REQUEST
import com.neighbourly.app.b_adapt.viewmodel.items.FilteredItemListViewModel.ItemTypeVS.SALE
import com.neighbourly.app.b_adapt.viewmodel.items.FilteredItemListViewModel.ItemTypeVS.SKILLSHARE
import com.neighbourly.app.c_business.usecase.items.ContentSyncUseCase
import com.neighbourly.app.d_entity.data.ItemType
import com.neighbourly.app.d_entity.data.OpException
import com.neighbourly.app.d_entity.interf.Db
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant

const val MAX_DESC_LEN = 70

class FilteredItemListViewModel(
    val database: Db,
    val syncItemsUseCase: ContentSyncUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(FilteredItemListViewState())
    val state: StateFlow<FilteredItemListViewState> = _state.asStateFlow()

    fun setFilters(type: ItemType?, householdId: Int?) {
        _state.update { it.copy(type = type, householdId = householdId) }
        refilter()
    }

    fun refresh(force: Boolean = false) {
        viewModelScope.launch {
            _state.update { it.copy(loading = true) }
            try {
                syncItemsUseCase.execute(force)
                refilter()
            } catch (e: OpException) {
                _state.update { it.copy(loading = false) }
            }
        }
    }

    fun refilter() {
        viewModelScope.launch {
            database.filterItems(_state.value.type, _state.value.householdId).let { items ->
                _state.update {
                    it.copy(
                        loading = false,
                        items = items.map {
                            ItemVS(
                                id = it.id,
                                name = it.name.orEmpty(),
                                description = it.description.orEmpty().let {
                                    if (it.length > MAX_DESC_LEN)
                                        it.substring(0, MAX_DESC_LEN) + "...";
                                    else it
                                },
                                imageUrl = it.images.values.randomOrNull(),
                                type = it.type.toItemTypeVS(),
                                imgCount = it.images.size,
                                fileCount = it.files.size,
                                endsSec = it.endTs?.let { Instant.now().epochSecond.toInt() - it },
                            )
                        })
                }
            }
        }
    }

    data class FilteredItemListViewState(
        val type: ItemType? = null,
        val householdId: Int? = null,
        val loading: Boolean = false,
        val items: List<ItemVS> = emptyList()
    )

    data class ItemVS(
        val id: Int,
        val name: String,
        val description: String,
        val imageUrl: String? = null,
        val type: ItemTypeVS,
        val imgCount: Int = 0,
        val fileCount: Int = 0,
        val endsSec: Int? = null,
    )


    enum class ItemTypeVS {
        INFO,
        DONATION,
        BARTER,
        SALE,
        EVENT,
        NEED,
        REQUEST,
        SKILLSHARE;
    }

    fun ItemType.toItemTypeVS() =
        when (this) {
            ItemType.INFO -> INFO
            ItemType.DONATION -> DONATION
            ItemType.BARTER -> BARTER
            ItemType.SALE -> SALE
            ItemType.EVENT -> EVENT
            ItemType.NEED -> NEED
            ItemType.REQUEST -> REQUEST
            ItemType.SKILLSHARE -> SKILLSHARE
        }
}