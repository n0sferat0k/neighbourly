package com.neighbourly.app.a_device.ui.atomic.page

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.neighbourly.app.KoinProvider
import com.neighbourly.app.a_device.ui.atomic.template.FilteredItemListTemplate
import com.neighbourly.app.b_adapt.viewmodel.bean.ItemTypeVS
import com.neighbourly.app.b_adapt.viewmodel.items.FilteredItemListViewModel
import com.neighbourly.app.b_adapt.viewmodel.navigation.MainContent
import com.neighbourly.app.b_adapt.viewmodel.navigation.NavigationViewModel

@Composable
fun FilteredItemListPage(
    type: ItemTypeVS? = null,
    householdId: Int? = null,
    itemIds: List<Int>? = null,
    showExpired: Boolean = false,
    showOwnHousehold: Boolean = false,
    viewModel: FilteredItemListViewModel = viewModel { KoinProvider.KOIN.get<FilteredItemListViewModel>() },
    navigationViewModel: NavigationViewModel = viewModel { KoinProvider.KOIN.get<NavigationViewModel>() }
) {
    val state by viewModel.state.collectAsState()
    LaunchedEffect(type, householdId, showExpired) {
        viewModel.setFilters(type, householdId, itemIds, showExpired, showOwnHousehold)
    }
    FilteredItemListTemplate(
        state = state,
        onDeleteItem = viewModel::onDeleteItem,
        onSelectItem = navigationViewModel::goToItemDetails,
        onSelectHousehold = navigationViewModel::goToHouseholdDetails,
        refresh = { viewModel.refresh(true) },
        add = { navigationViewModel.goToMainPage(MainContent.PublishStuff(type)) }
    )
}