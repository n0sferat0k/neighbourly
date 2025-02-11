package com.neighbourly.app.a_device.ui.atomic.organism.box

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neighbourly.app.a_device.ui.atomic.atom.FriendlyButton
import com.neighbourly.app.a_device.ui.atomic.atom.FriendlyText
import com.neighbourly.app.a_device.ui.atomic.molecule.household.HouseholdBadge
import com.neighbourly.app.a_device.ui.generateQrCode
import com.neighbourly.app.b_adapt.viewmodel.bean.BoxShareVS
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.box_share_household
import neighbourly.composeapp.generated.resources.box_share_scan_code
import neighbourly.composeapp.generated.resources.box_share_use_link
import neighbourly.composeapp.generated.resources.copy_to_clipboard
import org.jetbrains.compose.resources.stringResource

@Composable
fun OrganismBoxShare(shareBox: BoxShareVS, onHouseholdClick: (householdId: Int) -> Unit) {
    val clipboard = LocalClipboardManager.current
    val uriHandler = LocalUriHandler.current

    val url = "http://neighbourly.go.ro:8080/box/openbox?token=${shareBox.token}"

    FriendlyText(
        modifier = Modifier.fillMaxWidth(),
        text = shareBox.name,
        bold = true,
        fontSize = 26.sp
    )

    if (shareBox.household == null) {
        FriendlyText(text = stringResource(Res.string.box_share_scan_code), bold = true)

        Image(
            painter = BitmapPainter(generateQrCode(shareBox.token, 800)),
            contentDescription = "QR Code",
            modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            FriendlyText(text = stringResource(Res.string.box_share_use_link), bold = true)

            FriendlyText(modifier = Modifier.clickable {
                uriHandler.openUri(url)
            }, text = url)

            FriendlyButton(
                text = stringResource(Res.string.copy_to_clipboard),
            ) {
                clipboard.setText(buildAnnotatedString { append(url) })
            }
        }
    } else {
        FriendlyText(text = stringResource(Res.string.box_share_household), bold = true)

        HouseholdBadge(household = shareBox.household, sizeMultiplyer = 3) {
            onHouseholdClick(
                shareBox.household.id
            )
        }
    }
}