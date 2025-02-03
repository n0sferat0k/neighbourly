package com.neighbourly.app.a_device.ui.atomic.organism.chat

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import com.neighbourly.app.a_device.ui.atomic.molecule.item.ItemHouseholdBadge
import com.neighbourly.app.b_adapt.viewmodel.bean.HouseholdVS

@Composable
fun MessageBubbleWithOrigin(
    modifier: Modifier = Modifier,
    senderHouse: HouseholdVS?,
    sender: String,
    text: String,
    onHouseholdClick: () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth().wrapContentHeight(),
        shape = RoundedCornerShape(8.dp),
        elevation = 4.dp
    ) {
        Row(modifier = Modifier.padding(4.dp)) {
            senderHouse?.let {
                ItemHouseholdBadge(
                    householdImage = it.imageurl,
                    householdName = it.name,
                    onClick = onHouseholdClick
                )
            }

            Column {
                FriendlyText(
                    modifier = Modifier.padding(4.dp),
                    text = sender,
                    bold = true,
                    fontSize = 14.sp
                )
                FriendlyText(
                    modifier = Modifier.padding(4.dp),
                    text = text,
                    bold = false,
                    fontSize = 14.sp
                )
            }
        }
    }
}