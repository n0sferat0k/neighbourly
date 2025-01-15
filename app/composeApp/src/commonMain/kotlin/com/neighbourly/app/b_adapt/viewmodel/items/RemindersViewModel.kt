package com.neighbourly.app.b_adapt.viewmodel.items

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neighbourly.app.b_adapt.viewmodel.bean.ReminderVS
import com.neighbourly.app.d_entity.data.ItemType.REMINDER
import com.neighbourly.app.d_entity.interf.Db
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant

class RemindersViewModel(
    val database: Db,
) : ViewModel() {

    private val _state = MutableStateFlow(RemindersViewState())
    val state: StateFlow<RemindersViewState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            database.filterItems(type = REMINDER).let { items ->
                _state.update {
                    it.copy(reminders = items.map { item ->
                        ReminderVS(
                            id = item.id,
                            name = item.name.orEmpty(),
                            times = item.description?.split(",")?.map {
                                Instant.fromEpochSeconds(it.toLong())
                            } ?: emptyList())
                    })
                }
            }
        }
    }

    data class RemindersViewState(
        val reminders: List<ReminderVS> = emptyList()
    )

}
