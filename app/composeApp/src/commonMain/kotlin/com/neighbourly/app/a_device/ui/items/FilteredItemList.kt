package com.neighbourly.app.a_device.ui.items

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.neighbourly.app.KoinProvider
import com.neighbourly.app.a_device.ui.BoxContent
import com.neighbourly.app.a_device.ui.BoxHeader
import com.neighbourly.app.a_device.ui.CurlyText
import com.neighbourly.app.b_adapt.viewmodel.items.FilteredItemListViewModel
import com.neighbourly.app.b_adapt.viewmodel.navigation.NavigationViewModel
import com.neighbourly.app.d_entity.data.ItemType
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.under_construction
import org.jetbrains.compose.resources.stringResource

@Composable
fun FilteredItemList(
    type: ItemType,
    viewModel: FilteredItemListViewModel = viewModel { KoinProvider.KOIN.get<FilteredItemListViewModel>() },
    navigationViewModel: NavigationViewModel = viewModel { KoinProvider.KOIN.get<NavigationViewModel>() }
) {
    val state by viewModel.state.collectAsState()
    val navigation by navigationViewModel.state.collectAsState()

    LaunchedEffect(type) {
        viewModel.setType(type)
    }

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        BoxHeader(Modifier.align(Alignment.Start), busy = state.loading) {
            viewModel.refresh(true)
        }

        BoxContent(modifier = Modifier.weight(1f)) {
            CurlyText(
                modifier = Modifier.padding(start = 10.dp),
                text = stringResource(Res.string.under_construction),
                fontSize = 24.sp,
            )
        }
    }

}