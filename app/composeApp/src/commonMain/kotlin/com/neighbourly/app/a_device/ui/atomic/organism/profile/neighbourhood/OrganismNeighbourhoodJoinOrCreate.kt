package com.neighbourly.app.a_device.ui.atomic.organism.profile.neighbourhood

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.unit.dp
import com.neighbourly.app.a_device.ui.atomic.atom.FriendlyButton
import com.neighbourly.app.a_device.ui.atomic.atom.FriendlyText
import com.neighbourly.app.a_device.ui.generateQrCode
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.cannot_create_neighbourhood
import neighbourly.composeapp.generated.resources.create
import neighbourly.composeapp.generated.resources.create_neighbourhood
import neighbourly.composeapp.generated.resources.join_neighbourhood
import org.jetbrains.compose.resources.stringResource

@Composable
fun OrganismNeighbourhoodJoinOrCreate(userQr: String?, canCreate: Boolean, onCreate: () -> Unit) {
    FriendlyText(text = stringResource(Res.string.join_neighbourhood))

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
    if (canCreate) {
        FriendlyText(text = stringResource(Res.string.create_neighbourhood))

        FriendlyButton(text = stringResource(Res.string.create)) {
            onCreate()
        }
    } else {
        FriendlyText(text = stringResource(Res.string.cannot_create_neighbourhood))
    }
}