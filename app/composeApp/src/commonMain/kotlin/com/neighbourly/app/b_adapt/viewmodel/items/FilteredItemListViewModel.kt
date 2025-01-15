package com.neighbourly.app.b_adapt.viewmodel.items

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neighbourly.app.b_adapt.viewmodel.bean.ItemVS
import com.neighbourly.app.b_adapt.viewmodel.bean.toItemTypeVS
import com.neighbourly.app.c_business.usecase.content.ContentSyncUseCase
import com.neighbourly.app.c_business.usecase.content.FilterItemsUseCase
import com.neighbourly.app.c_business.usecase.content.ItemManagementUseCase
import com.neighbourly.app.d_entity.data.ItemType
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

    fun setFilters(type: ItemType?, householdId: Int?, showExpired: Boolean) {
        _state.update { it.copy(type = type, householdId = householdId, showExpired = showExpired) }
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
                _state.value.type,
                _state.value.householdId,
                _state.value.showExpired
            )
                .let { itemsAndHouses ->
                    _state.update {
                        it.copy(loading = false, items = itemsAndHouses.map { (item, house) ->
                            ItemVS(
                                id = item.id!!,
                                name = item.name.orEmpty(),
                                description = when (it.type) {
                                    ItemType.REMINDER -> kotlin.runCatching {
                                        item.description?.split(",")
                                            ?.map { fromEpochSeconds(it.toLong()).toDateString() }
                                            ?.joinToString(", ").orEmpty()
                                    }.getOrDefault("")

                                    else -> item.description.orEmpty()
                                }.let {
                                    if (it.length > MAX_DESC_LEN) it.substring(
                                        0,
                                        MAX_DESC_LEN
                                    ) + "...";
                                    else it
                                },
                                imageUrl = item.images.randomOrNull()?.url,
                                type = item.type.toItemTypeVS(),
                                imgCount = item.images.size,
                                fileCount = item.files.size,
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
                                deletable = item.householdId == myHouseholdId,
                                householdImage = house?.imageurl,
                                householdName = house?.name
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

    companion object {
        const val MAX_DESC_LEN = 50
        const val MINUTE_IN_SECONDS = 60
        const val HOUR_IN_SECONDS = MINUTE_IN_SECONDS * 60
        const val DAY_IN_SECONDS = HOUR_IN_SECONDS * 24
    }
}