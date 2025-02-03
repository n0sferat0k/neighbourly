package com.neighbourly.app.a_device.ui.atomic.molecule.chat

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neighbourly.app.a_device.ui.atomic.atom.FriendlyText

@Composable
fun MessageBubbleSimple(text: String) {
    Card(
        modifier = Modifier.fillMaxWidth().wrapContentHeight(),
        shape = RoundedCornerShape(8.dp),
        elevation = 4.dp
    ) {
        FriendlyText(modifier = Modifier.padding(4.dp), text = text, bold = true, fontSize = 14.sp)
    }
}