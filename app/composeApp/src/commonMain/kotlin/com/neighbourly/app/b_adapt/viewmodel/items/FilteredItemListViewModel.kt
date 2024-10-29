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
import com.neighbourly.app.c_business.usecase.content.ContentSyncUseCase
import com.neighbourly.app.c_business.usecase.content.ItemManagementUseCase
import com.neighbourly.app.d_entity.data.ItemType
import com.neighbourly.app.d_entity.data.OpException
import com.neighbourly.app.d_entity.interf.Db
import com.neighbourly.app.d_entity.interf.SessionStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant

class FilteredItemListViewModel(
    val database: Db,
    val store: SessionStore,
    val syncItemsUseCase: ContentSyncUseCase,
    val itemManagementUseCase: ItemManagementUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(FilteredItemListViewState())
    val state: StateFlow<FilteredItemListViewState> = _state.asStateFlow()

    fun setFilters(type: ItemType?, householdId: Int?, showExpired: Boolean) {
        _state.update { it.copy(type = type, householdId = householdId, showExpired = showExpired) }
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

    fun onDeleteItem(itemId: Int) {
        viewModelScope.launch {
            itemManagementUseCase.delete(itemId)
            refilter()
        }
    }

    fun refilter() {
        val now = Instant.now().epochSecond.toInt()

        viewModelScope.launch {
            val myHouseholdId = store.user?.household?.householdid

            database.filterItems(_state.value.type, _state.value.householdId)

                .let { items ->
                    val filteredItems = if (!_state.value.showExpired) {
                        items.filter { it.id != null && (it.endTs == null || it.endTs == 0 || it.endTs > now) }
                    } else items

                    _state.update {
                        it.copy(loading = false, items = filteredItems.map {
                            ItemVS(
                                id = it.id!!,
                                name = it.name.orEmpty(),
                                description = it.description.orEmpty().let {
                                    if (it.length > MAX_DESC_LEN) it.substring(
                                        0,
                                        MAX_DESC_LEN
                                    ) + "...";
                                    else it
                                },
                                imageUrl = it.images.values.randomOrNull(),
                                type = it.type.toItemTypeVS(),
                                imgCount = it.images.size,
                                fileCount = it.files.size,
                                expLabel = it.endTs?.let {
                                    if (it > 0) {
                                        val remainingSeconds = it - now
                                        if (remainingSeconds > MINUTE_IN_SECONDS) {
                                            if (remainingSeconds < HOUR_IN_SECONDS) {
                                                "${remainingSeconds / MINUTE_IN_SECONDS} min"
                                            } else if (remainingSeconds < DAY_IN_SECONDS) {
                                                "${remainingSeconds / HOUR_IN_SECONDS} hr"
                                            } else {
                                                "${remainingSeconds / DAY_IN_SECONDS} d"
                                            }
                                        } else "exp"
                                    } else null
                                },
                                deletable = it.householdId == myHouseholdId
                            )
                        })
                    }
                }
        }
    }

    data class FilteredItemListViewState(
        val type: ItemType? = null,
        val householdId: Int? = null,
        val showExpired: Boolean = false,
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
        val expLabel: String? = null,
        val deletable: Boolean = false,
    )


    enum class ItemTypeVS {
        INFO, DONATION, BARTER, SALE, EVENT, NEED, REQUEST, SKILLSHARE;
    }

    fun ItemType.toItemTypeVS() = when (this) {
        ItemType.INFO -> INFO
        ItemType.DONATION -> DONATION
        ItemType.BARTER -> BARTER
        ItemType.SALE -> SALE
        ItemType.EVENT -> EVENT
        ItemType.NEED -> NEED
        ItemType.REQUEST -> REQUEST
        ItemType.SKILLSHARE -> SKILLSHARE
    }

    companion object {
        const val MAX_DESC_LEN = 70
        const val MINUTE_IN_SECONDS = 60
        const val HOUR_IN_SECONDS = MINUTE_IN_SECONDS * 60
        const val DAY_IN_SECONDS = HOUR_IN_SECONDS * 24
    }
}