package com.neighbourly.app.a_device.ui.atomic.page

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.neighbourly.app.KoinProvider
import com.neighbourly.app.a_device.ui.atomic.template.RemidersTemplate
import com.neighbourly.app.b_adapt.viewmodel.items.RemindersViewModel

@Composable
fun RemindersPage(viewModel: RemindersViewModel = viewModel { KoinProvider.KOIN.get<RemindersViewModel>() }) {
    val state by viewModel.state.collectAsState()

    RemidersTemplate(state.reminders)
}