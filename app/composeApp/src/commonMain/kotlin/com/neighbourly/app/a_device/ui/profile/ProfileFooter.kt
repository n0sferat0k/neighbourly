package com.neighbourly.app.a_device.ui.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.neighbourly.app.KoinProvider
import com.neighbourly.app.a_device.ui.utils.AppColors
import com.neighbourly.app.a_device.ui.atomic.atom.FriendlyText
import com.neighbourly.app.b_adapt.viewmodel.profile.ProfileFooterViewModel
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.everywhere
import neighbourly.composeapp.generated.resources.here
import neighbourly.composeapp.generated.resources.logout
import org.jetbrains.compose.resources.stringResource

@Composable
fun ProfileFooter(viewModel: ProfileFooterViewModel = viewModel { KoinProvider.KOIN.get<ProfileFooterViewModel>() }) {
    val state by viewModel.state.collectAsState()

    Row(
        modifier = Modifier.padding(bottom = 10.dp, end = 20.dp),
    ) {
        if (state.loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp).align(Alignment.Bottom).padding(end = 5.dp),
                color = AppColors.primary,
            )
        }
        FriendlyText(text = stringResource(Res.string.logout) + ":")
        FriendlyText(
            modifier =
                Modifier
                    .padding(start = 15.dp, end = 5.dp)
                    .clickable(onClick = {
                        viewModel.onLogout(logoutAll = false)
                    }),
            text = stringResource(Res.string.here),
            bold = true,
        )
        FriendlyText(text = "/")
        FriendlyText(
            modifier =
                Modifier
                    .padding(start = 5.dp)
                    .clickable(onClick = {
                        viewModel.onLogout(logoutAll = true)
                    }),
            text = stringResource(Res.string.everywhere),
            bold = true,
        )
    }
}
