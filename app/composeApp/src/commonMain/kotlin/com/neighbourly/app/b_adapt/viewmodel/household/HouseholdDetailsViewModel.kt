package com.neighbourly.app.b_adapt.viewmodel.household

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neighbourly.app.b_adapt.viewmodel.bean.HouseholdVS
import com.neighbourly.app.b_adapt.viewmodel.bean.ItemAugmentVS
import com.neighbourly.app.b_adapt.viewmodel.bean.ItemVS
import com.neighbourly.app.b_adapt.viewmodel.bean.toHouseholdVS
import com.neighbourly.app.b_adapt.viewmodel.bean.toItemVS
import com.neighbourly.app.b_adapt.viewmodel.bean.toMemberVS
import com.neighbourly.app.b_adapt.viewmodel.items.FilteredItemListViewModel.Companion.DAY_IN_SECONDS
import com.neighbourly.app.b_adapt.viewmodel.items.FilteredItemListViewModel.Companion.HOUR_IN_SECONDS
import com.neighbourly.app.b_adapt.viewmodel.items.FilteredItemListViewModel.Companion.MINUTE_IN_SECONDS
import com.neighbourly.app.d_entity.interf.Db
import com.neighbourly.app.d_entity.interf.SessionStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class HouseholdDetailsViewModel(
    private val database: Db,
    val store: SessionStore,
) : ViewModel() {

    private val _state = MutableStateFlow(HouseholdDetailsViewState())
    val state: StateFlow<HouseholdDetailsViewState> = _state.asStateFlow()

    fun setHousehold(householdId: Int) {
        viewModelScope.launch {
            val now = Clock.System.now().epochSeconds.toInt()
            val members = database.getUsers().filter { it.householdid == householdId }
                .map {
                    it.toMemberVS().let {
                        it.copy(
                            muted = store.user?.mutedUsers?.contains(it.id) ?: false
                        )
                    }
                }

            val household =
                database.getHousehold(householdId).toHouseholdVS().copy(
                    members = members,
                    muted = store.user?.mutedHouseholds?.contains(householdId) ?: false
                )
            val items = database.filterItems(householdId = householdId).map { item ->
                item.toItemVS().copy(
                    augmentation = ItemAugmentVS(
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
                    )
                )
            }
            _state.update { it.copy(household = household, items = items) }
        }
    }

    fun onMuteHouse(muted: Boolean) {
        _state.value.household?.id?.let {
            store.muteHousehold(it, muted)
            _state.update { it.copy(household = it.household?.copy(muted = muted)) }
        }
    }

    fun onMuteHouseMember(id: Int, muted: Boolean) {
        store.mutePerson(id, muted)
        _state.update {
            it.copy(household = it.household?.copy(members = it.household.members.map { member ->
                member.takeIf { it.id == id }?.copy(muted = muted)
                    ?: member
            }))
        }
    }

    data class HouseholdDetailsViewState(
        val household: HouseholdVS? = null,
        val items: List<ItemVS> = emptyList(),
    )
}
