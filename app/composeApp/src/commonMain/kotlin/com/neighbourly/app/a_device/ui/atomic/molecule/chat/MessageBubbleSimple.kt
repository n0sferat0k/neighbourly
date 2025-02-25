package com.neighbourly.app.a_device.ui.atomic.molecule.chat

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neighbourly.app.a_device.ui.LinkType
import com.neighbourly.app.a_device.ui.TextSegment
import com.neighbourly.app.a_device.ui.atomic.atom.FriendlyText
import com.neighbourly.app.a_device.ui.parseCustomText

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MessageBubbleSimple(
    modifier: Modifier = Modifier,
    text: String,
    parse: Boolean = false,
    onItemSelect: (itemId: Int) -> Unit = {},
    onHouseholdSelect: (itemId: Int) -> Unit = {}
) {
    Card(
        modifier = modifier.fillMaxWidth().wrapContentHeight(),
        shape = RoundedCornerShape(8.dp),
        elevation = 4.dp
    ) {
        if (parse) {
            FlowRow(modifier = Modifier.padding(8.dp)) {
                parseCustomText(text).forEach { segment ->
                    when (segment) {
                        is TextSegment.Link -> FriendlyText(
                            modifier = Modifier.clickable {
                                when (segment.type) {
                                    LinkType.ITEM -> onItemSelect(segment.id)
                                    LinkType.HOUSEHOLD -> onHouseholdSelect(segment.id)
                                }
                            },
                            text = segment.text,
                            bold = true,
                            fontSize = 14.sp
                        )

                        is TextSegment.Plain -> FriendlyText(
                            text = segment.text,
                            bold = false,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        } else {
            Box(modifier = Modifier.padding(8.dp)) {
                FriendlyText(
                    text = text,
                    bold = false,
                    fontSize = 14.sp
                )
            }
        }
    }
}