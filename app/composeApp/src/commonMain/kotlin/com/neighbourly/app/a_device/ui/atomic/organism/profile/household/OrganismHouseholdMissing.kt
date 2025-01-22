package com.neighbourly.app.a_device.ui.atomic.organism.profile.household

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.unit.dp
import com.neighbourly.app.a_device.ui.atomic.atom.FriendlyText
import com.neighbourly.app.a_device.ui.generateQrCode
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.create_from_scratch
import neighbourly.composeapp.generated.resources.invite_or_create_household
import org.jetbrains.compose.resources.stringResource

@Composable
fun OrganismHouseholdMissing(userQr: String?, onCreateHousehold: () -> Unit) {
    Column {
        FriendlyText(text = stringResource(Res.string.invite_or_create_household))

        userQr.takeIf { !it.isNullOrBlank() }?.let {
            Image(
                painter = BitmapPainter(generateQrCode(it, 400)),
                contentDescription = "QR Code",
                modifier =
                Modifier
                    .fillMaxSize()
                    .padding(16.dp),
            )
        }

        FriendlyText(
            modifier =
            Modifier.clickable {
                onCreateHousehold()
            },
            bold = true,
            text = stringResource(Res.string.create_from_scratch),
        )
    }
}