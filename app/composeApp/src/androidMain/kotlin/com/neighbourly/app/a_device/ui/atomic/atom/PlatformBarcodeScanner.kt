package com.neighbourly.app.a_device.ui.atomic.atom

import android.Manifest.permission.CAMERA
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ExperimentalLensFacing
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.neighbourly.app.a_device.ui.AppColors
import com.neighbourly.app.a_device.ui.atomic.molecule.PermissionGatedContent

@ExperimentalLensFacing
@ExperimentalGetImage
@Composable
actual fun PlatformBarcodeScanner(
    modifier: Modifier,
    onDone: ((String) -> Unit),
) {
    var isResultDelivered by remember { mutableStateOf(false) }
    var lensFacing by remember { mutableIntStateOf(CameraSelector.LENS_FACING_BACK) }
    val rotateLensFacing = {
        lensFacing =
            when (lensFacing) {
                CameraSelector.LENS_FACING_FRONT -> CameraSelector.LENS_FACING_BACK
                CameraSelector.LENS_FACING_BACK -> CameraSelector.LENS_FACING_EXTERNAL
                else -> CameraSelector.LENS_FACING_FRONT
            }
    }
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val scanner =
        remember {
            BarcodeScanning.getClient(
                BarcodeScannerOptions.Builder().enableAllPotentialBarcodes().build(),
            )
        }
    val imageAnalysis =
        remember {
            ImageAnalysis
                .Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .apply {
                    setAnalyzer(ContextCompat.getMainExecutor(context)) { imageProxy ->
                        imageProxy.image?.let {
                            scanner
                                .process(
                                    InputImage.fromMediaImage(
                                        it,
                                        imageProxy.imageInfo.rotationDegrees,
                                    ),
                                ).addOnSuccessListener {
                                    it
                                        .firstOrNull()
                                        ?.displayValue
                                        ?.takeIf { it.isNotBlank() && !isResultDelivered }
                                        ?.let {
                                            onDone(it)
                                            isResultDelivered = true
                                        }
                                }.addOnCompleteListener { imageProxy.close() }
                        }
                    }
                }
        }
    PermissionGatedContent(permissions = listOf(CAMERA)) {
        Box(
            modifier = modifier
                .wrapContentSize()
                .widthIn(max = 300.dp)
                .heightIn(max = 400.dp)
        ) {
            AndroidView(
                modifier = Modifier
                    .widthIn(max = 300.dp)
                    .heightIn(max = 400.dp)
                    .align(Alignment.Center),
                factory = { PreviewView(context) },
            ) { view ->
                val cameraProvider = ProcessCameraProvider.getInstance(context).get()
                val preview = Preview.Builder().build()
                val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()

                cameraProvider.unbindAll()
                try {
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageAnalysis,
                    )
                } catch (e: IllegalArgumentException) {
                    rotateLensFacing()
                }

                preview.setSurfaceProvider(view.surfaceProvider)
            }
            IconButton(
                modifier = Modifier.padding(end = 24.dp),
                onClick = {
                    rotateLensFacing()
                },
            ) {
                Icon(
                    imageVector = Icons.Default.Cameraswitch,
                    contentDescription = null,
                    tint = AppColors.primary,
                )
            }
        }
    }
}
