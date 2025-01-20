package com.neighbourly.app.a_device.ui.atomic.organism.util

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.neighbourly.app.a_device.ui.atomic.molecule.card.CardFooter
import com.neighbourly.app.a_device.ui.atomic.molecule.card.CardHeader
import com.neighbourly.app.a_device.ui.atomic.molecule.card.CardScrollableContent
import com.neighbourly.app.a_device.ui.atomic.molecule.card.CardStaticContent
import com.neighbourly.app.a_device.ui.atomic.molecule.card.RoundedCornerCard

@Composable
fun OrganismContentBubble(
    scrollable: Boolean = false,
    busy: Boolean = false,
    content: @Composable ColumnScope.() -> Unit,
    footerContent: @Composable RowScope.() -> Unit,
    refresh: (() -> Unit)? = null
) {
    RoundedCornerCard {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            CardHeader(
                modifier = Modifier.fillMaxWidth().align(Alignment.Start),
                busy = busy,
                refresh = refresh
            )

            if (scrollable) {
                CardScrollableContent(modifier = Modifier.fillMaxWidth().weight(1f)) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, content = content)
                }
            } else {
                CardStaticContent(modifier = Modifier.fillMaxWidth().weight(1f)) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, content = content)
                }
            }

            CardFooter(content = footerContent)
        }
    }
}