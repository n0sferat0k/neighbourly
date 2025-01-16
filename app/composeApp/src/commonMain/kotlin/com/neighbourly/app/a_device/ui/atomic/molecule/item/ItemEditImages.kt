package com.neighbourly.app.a_device.ui.atomic.molecule.item

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import com.darkrockstudios.libraries.mpfilepicker.FilePicker
import com.neighbourly.app.a_device.ui.atomic.atom.FriendlyText
import com.neighbourly.app.b_adapt.viewmodel.bean.AttachmentVS
import com.neighbourly.app.b_adapt.viewmodel.bean.MemImgVS
import com.neighbourly.app.loadImageFromFile
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.add_image
import neighbourly.composeapp.generated.resources.images
import org.jetbrains.compose.resources.stringResource

@Composable
fun ItemEditImages(
    hidden: Boolean = false,
    images: List<AttachmentVS>,
    newImages: List<MemImgVS>,
    onDeleteImage: (imageId: Int) -> Unit,
    onImageSelected: (imageId: Int) -> Unit,
    onNewImages: (newImages: List<MemImgVS>) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    var showImageFilePicker by remember { mutableStateOf(false) }

    FilePicker(show = showImageFilePicker, fileExtensions = listOf("jpg", "png")) { file ->
        showImageFilePicker = false

        file?.platformFile?.toString()?.let {
            val img = loadImageFromFile(it, 400)
            if (img != null) {
                onNewImages(newImages + MemImgVS(it, img))
            }
        }
    }

    AnimatedVisibility(!hidden) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            FriendlyText(text = stringResource(Res.string.images))
            FriendlyText(modifier = Modifier.clickable {
                focusManager.clearFocus(true)
                showImageFilePicker = true
            }, text = stringResource(Res.string.add_image), bold = true)
        }

        if (images.size > 0 || newImages.size > 0) {
            ImageGrid(
                images = images,
                newImages = newImages,
                deleteNew = { delImg ->
                    onNewImages(newImages.filter { it != delImg })
                },
                delete = {
                    onDeleteImage(it.id)
                },
                select = {
                    onImageSelected(it.id)
                }
            )
        }
    }
}