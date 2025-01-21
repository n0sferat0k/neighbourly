package com.neighbourly.app.a_device.ui.atomic.template

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.neighbourly.app.a_device.ui.atomic.atom.FriendlyText
import com.neighbourly.app.a_device.ui.atomic.organism.util.OrganismContentBubble
import com.neighbourly.app.b_adapt.viewmodel.bean.ReminderVS
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun RemidersTemplate(reminders: List<ReminderVS>) {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

    OrganismContentBubble(
        scrollable = true,
        content = {
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                reminders.forEach {
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
        },
        footerContent = {}
    )
}