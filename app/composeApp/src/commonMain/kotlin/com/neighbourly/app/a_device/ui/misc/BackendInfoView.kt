package com.neighbourly.app.a_device.ui.misc

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neighbourly.app.KoinProvider
import com.neighbourly.app.a_device.ui.utils.FriendlyText
import com.neighbourly.app.a_device.ui.utils.FriendlyErrorText
import com.neighbourly.app.a_device.ui.utils.generateQrCode
import com.neighbourly.app.b_adapt.viewmodel.SignalViewModel
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.last_error
import neighbourly.composeapp.generated.resources.scan_qr_for_app_install
import org.jetbrains.compose.resources.stringResource

@Composable
fun BackendInfoView(viewModel: SignalViewModel = androidx.lifecycle.viewmodel.compose.viewModel { KoinProvider.KOIN.get<SignalViewModel>() }) {
    val state by viewModel.state.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {

        FriendlyText(
            modifier = Modifier.padding(20.dp),
            text = stringResource(Res.string.scan_qr_for_app_install),
            fontSize = 22.sp,
        )

        Image(
            painter = BitmapPainter(
                generateQrCode(
                    "http://neighbourly.go.ro/releases/neighbourly-1.0.2.apk",
                    400
                )
            ),
            contentDescription = "QR Code",
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        )
        if (state.lastError != null) {
            FriendlyText(
                modifier = Modifier.padding(20.dp),
                text = stringResource(Res.string.last_error),
                fontSize = 22.sp,
            )

            FriendlyErrorText(state.lastError.orEmpty())
        }
    }
}