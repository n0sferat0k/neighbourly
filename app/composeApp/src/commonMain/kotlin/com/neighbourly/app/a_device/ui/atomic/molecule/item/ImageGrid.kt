package com.neighbourly.app.a_device.ui.atomic.molecule.item

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.neighbourly.app.a_device.ui.AppColors
import com.neighbourly.app.a_device.ui.atomic.molecule.misc.SwipeToDeleteContainer
import com.neighbourly.app.a_device.ui.atomic.organism.util.OrganismAlertDialog
import com.neighbourly.app.b_adapt.viewmodel.bean.AttachmentVS
import com.neighbourly.app.b_adapt.viewmodel.bean.MemImgVS
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.confirm_deleteing_image
import neighbourly.composeapp.generated.resources.confirm_new_image
import neighbourly.composeapp.generated.resources.deleteing_image
import neighbourly.composeapp.generated.resources.new_image
import neighbourly.composeapp.generated.resources.newbadge
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ImageGrid(
    images: List<AttachmentVS>,
    newImages: List<MemImgVS>,
    deleteNew: ((MemImgVS) -> Unit)? = null,
    delete: ((AttachmentVS) -> Unit)? = null,
    select: ((AttachmentVS) -> Unit)? = null
) {
    var showRemoveImageAlertForId by remember { mutableStateOf(-1) }
    var showNewImageAlert by remember { mutableStateOf(false) }
    val badge = painterResource(Res.drawable.newbadge)

    if (showNewImageAlert) {
        OrganismAlertDialog(
            title = stringResource(Res.string.new_image),
            text = stringResource(Res.string.confirm_new_image),
            ok = {
                showNewImageAlert = false
            },
        )
    }

    if (showRemoveImageAlertForId != -1) {
        OrganismAlertDialog(
            title = stringResource(Res.string.deleteing_image),
            text = stringResource(Res.string.confirm_deleteing_image),
            ok = {
                images.firstOrNull { it.id == showRemoveImageAlertForId }
                    ?.let { delete?.invoke(it) }
                showRemoveImageAlertForId = -1
            },
            cancel = {
                showRemoveImageAlertForId = -1
            }
        )
    }

    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        images.forEach { image ->
            SwipeToDeleteContainer(
                modifier = Modifier.size(84.dp),
                onDelete = delete?.let {
                    {
                        showRemoveImageAlertForId = image.id
                    }
                }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f),
                    shape = RoundedCornerShape(4.dp),
                    elevation = 4.dp
                ) {
                    KamelImage(
                        modifier = Modifier.fillMaxSize().clickable {
                            select?.invoke(image)
                        },
                        resource = asyncPainterResource(data = image.url),
                        contentDescription = "Item Image",
                        contentScale = ContentScale.Crop,
                        onLoading = { progress ->
                            CircularProgressIndicator(
                                progress = progress,
                                color = AppColors.primary,
                            )
                        },
                    )
                }
            }
        }
        newImages.forEach { memImg ->
            SwipeToDeleteContainer(
                modifier = Modifier.size(84.dp),
                onDelete = {
                    deleteNew?.invoke(memImg)
                }
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f),
                    shape = RoundedCornerShape(4.dp),
                    elevation = 4.dp
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Image(
                            modifier = Modifier.fillMaxSize().clickable {
                                showNewImageAlert = true
                            },
                            painter = memImg.img,
                            contentDescription = "Item Image",
                            contentScale = ContentScale.Crop,
                        )
                        Image(
                            modifier = Modifier.size(36.dp).padding(4.dp)
                                .align(Alignment.BottomEnd),
                            painter = badge,
                            contentScale = ContentScale.FillBounds,
                            contentDescription = "Item Image New Badge",
                            colorFilter = ColorFilter.tint(AppColors.primary),
                        )
                    }
                }
            }
        }
    }
}