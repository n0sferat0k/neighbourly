package com.neighbourly.app.a_device.ui.atomic.organism.info

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neighbourly.app.a_device.ui.atomic.atom.FriendlyText
import com.neighbourly.app.a_device.ui.generateQrCode
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.scan_qr_for_app_install
import neighbourly.composeapp.generated.resources.visit_page_for_app_install
import org.jetbrains.compose.resources.stringResource

@Composable
fun OrganismAppDownloadLink(appVersion: String) {
    val uriHandler = LocalUriHandler.current

    Column(
        modifier = Modifier.padding(top = 10.dp, bottom = 10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        FriendlyText(
            text = stringResource(Res.string.scan_qr_for_app_install),
            fontSize = 22.sp,
        )
        Image(
            painter = BitmapPainter(
                generateQrCode(
                    "http://neighbourlybox.com/releases/neighbourly-" + appVersion + ".apk",
                    600
                )
            ),
            contentDescription = "QR Code",
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        )
        FriendlyText(
            text = stringResource(Res.string.visit_page_for_app_install),
            fontSize = 22.sp,
        )
        FriendlyText(
            modifier = Modifier.clickable {
                uriHandler.openUri("http://neighbourlybox.com/releases/")
            },
            text = "http://neighbourlybox.com/releases/",
            fontSize = 22.sp,
            bold = true,
        )
    }
}