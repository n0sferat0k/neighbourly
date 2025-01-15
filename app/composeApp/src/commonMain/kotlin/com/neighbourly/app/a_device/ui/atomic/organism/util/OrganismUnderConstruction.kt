package com.neighbourly.app.a_device.ui.atomic.organism.util

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neighbourly.app.a_device.ui.atomic.molecule.card.CardHeader
import com.neighbourly.app.a_device.ui.atomic.atom.FriendlyText
import com.neighbourly.app.a_device.ui.atomic.molecule.card.RoundedCornerCard
import com.neighbourly.app.a_device.ui.atomic.molecule.card.CardStaticContent
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.under_construction
import org.jetbrains.compose.resources.stringResource

@Composable
fun OrganismUnderConstruction() {
    RoundedCornerCard {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            CardHeader(modifier = Modifier.align(Alignment.Start))

            CardStaticContent(modifier = Modifier.weight(1f)) {
                FriendlyText(
                    modifier = Modifier.padding(start = 10.dp),
                    text = stringResource(Res.string.under_construction),
                    fontSize = 22.sp,
                )
            }
        }
    }
}
