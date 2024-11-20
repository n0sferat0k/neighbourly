package com.neighbourly.app.a_device.ui

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
import com.neighbourly.app.a_device.ui.utils.BoxHeader
import com.neighbourly.app.a_device.ui.utils.BoxScrollableContent
import com.neighbourly.app.a_device.ui.utils.CurlyText
import com.neighbourly.app.b_adapt.viewmodel.navigation.NavigationViewModel
import com.neighbourly.app.b_adapt.viewmodel.profile.ProfileViewModel
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.under_construction
import org.jetbrains.compose.resources.stringResource

@Composable
fun UnderConstruction(
    navigationViewModel: NavigationViewModel = viewModel { KoinProvider.KOIN.get<NavigationViewModel>() },
    viewModel: ProfileViewModel = viewModel { KoinProvider.KOIN.get<ProfileViewModel>() },
) {
    val navigation by navigationViewModel.state.collectAsState()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.refresh()
    }

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        BoxHeader(modifier = Modifier.align(Alignment.Start), busy = state.loading) {
            viewModel.refresh()
        }

        BoxScrollableContent(modifier = Modifier.weight(1f)) {
            CurlyText(
                modifier = Modifier.padding(start = 10.dp),
                text = stringResource(Res.string.under_construction),
                fontSize = 24.sp,
            )
        }
    }
}
