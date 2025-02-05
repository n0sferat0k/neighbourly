package com.neighbourly.app.b_adapt.viewmodel.items

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neighbourly.app.b_adapt.viewmodel.bean.ItemAugmentVS
import com.neighbourly.app.b_adapt.viewmodel.bean.ItemTypeVS
import com.neighbourly.app.b_adapt.viewmodel.bean.ItemTypeVS.REMINDER
import com.neighbourly.app.b_adapt.viewmodel.bean.ItemVS
import com.neighbourly.app.b_adapt.viewmodel.bean.toHouseholdVS
import com.neighbourly.app.b_adapt.viewmodel.bean.toItemType
import com.neighbourly.app.b_adapt.viewmodel.bean.toItemTypeVS
import com.neighbourly.app.b_adapt.viewmodel.bean.toItemVS
import com.neighbourly.app.c_business.usecase.content.ContentSyncUseCase
import com.neighbourly.app.c_business.usecase.content.FilterItemsUseCase
import com.neighbourly.app.c_business.usecase.content.ItemManagementUseCase
import com.neighbourly.app.d_entity.data.OpException
import com.neighbourly.app.d_entity.interf.SessionStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.Instant.Companion.fromEpochSeconds
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import java.time.format.DateTimeFormatter

class FilteredItemListViewModel(
    val store: SessionStore,
    val syncItemsUseCase: ContentSyncUseCase,
    val itemManagementUseCase: ItemManagementUseCase,
    val filterItemsUseCase: FilterItemsUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(FilteredItemListViewState())
    val state: StateFlow<FilteredItemListViewState> = _state.asStateFlow()

    fun setFilters(
        type: ItemTypeVS?,
        householdId: Int?,
        itemIds: List<Int>?,
        showExpired: Boolean,
        showOwnHousehold: Boolean = false
    ) {
        val filterHouseholdId =
            if (showOwnHousehold) store.user?.household?.householdid else householdId
        _state.update {
            it.copy(
                type = type,
                householdId = filterHouseholdId,
                itemIds = itemIds,
                showExpired = showExpired
            )
        }
        refresh()
    }

    fun refresh(force: Boolean = false) {
        viewModelScope.launch {
            _state.update { it.copy(loading = true) }
            try {
                syncItemsUseCase.execute(force)
                refilter()
                _state.update { it.copy(loading = false) }
            } catch (e: OpException) {
                _state.update { it.copy(loading = false) }
            }
        }
    }

    fun onDeleteItem(itemId: Int) {
        viewModelScope.launch {
            _state.update { it.copy(loading = true) }
            try {
                itemManagementUseCase.delete(itemId)
                _state.update {
                    it.copy(
                        loading = false,
                        items = it.items.filter { it.id != itemId })
                }
            } catch (e: OpException) {
                _state.update { it.copy(loading = false) }
            }
        }
    }

    fun Instant.toDateString() =
        this.toLocalDateTime(TimeZone.currentSystemDefault())
            .toJavaLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))

    fun refilter() {
        viewModelScope.launch {
            val myHouseholdId = store.user?.household?.householdid
            val now = Clock.System.now().epochSeconds.toInt()

            filterItemsUseCase.filterItems(
                _state.value.type?.toItemType(),
                _state.value.householdId,
                _state.value.itemIds,
                _state.value.showExpired
            )
                .let { itemsAndHouses ->
                    _state.update {
                        it.copy(loading = false, items = itemsAndHouses.map { (item, house) ->
                            item.toItemVS().copy(augmentation = ItemAugmentVS(
                                expLabel = item.endTs.let {
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
                                imageUrl = item.images.randomOrNull()?.url,
                                deletable = item.householdId == myHouseholdId,
                                household = house?.toHouseholdVS(),
                                watched = store.user?.watchedItems?.contains(item.id) ?: false
                            ))
                        })
                    }
                }
        }
    }

    data class FilteredItemListViewState(
        val type: ItemTypeVS? = null,
        val householdId: Int? = null,
        val itemIds: List<Int>? = null,
        val showExpired: Boolean = false,
        val loading: Boolean = false,
        val items: List<ItemVS> = emptyList()
    )

    companion object {
        const val MAX_DESC_LEN = 50
        const val MINUTE_IN_SECONDS = 60
        const val HOUR_IN_SECONDS = MINUTE_IN_SECONDS * 60
        const val DAY_IN_SECONDS = HOUR_IN_SECONDS * 24
    }
}