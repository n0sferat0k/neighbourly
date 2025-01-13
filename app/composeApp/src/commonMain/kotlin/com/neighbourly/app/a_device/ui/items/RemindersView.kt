package com.neighbourly.app.a_device.ui.items

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.neighbourly.app.KoinProvider
import com.neighbourly.app.a_device.ui.utils.BoxHeader
import com.neighbourly.app.a_device.ui.utils.BoxScrollableContent
import com.neighbourly.app.a_device.ui.atomic.atom.FriendlyText
import com.neighbourly.app.b_adapt.viewmodel.items.RemindersViewModel
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun RemidersView(viewModel: RemindersViewModel = viewModel { KoinProvider.KOIN.get<RemindersViewModel>() }) {
    val state by viewModel.state.collectAsState()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        BoxHeader(Modifier.align(Alignment.Start))

        BoxScrollableContent(modifier = Modifier.weight(1f)) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(20.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                state.reminders.forEach {
                    FriendlyText(text = it.name, bold = true)
                    it.times.forEach {
                        FriendlyText(
                            modifier = Modifier.padding(start = 10.dp),
                            text = it.toLocalDateTime(TimeZone.currentSystemDefault())
                                .toJavaLocalDateTime().format(formatter),
                            bold = false
                        )
                    }
                }
            }
        }
    }
}