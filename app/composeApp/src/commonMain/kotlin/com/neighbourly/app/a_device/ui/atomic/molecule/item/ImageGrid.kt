package com.neighbourly.app.a_device.ui.atomic.molecule.item

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.neighbourly.app.a_device.ui.AppColors
import com.neighbourly.app.a_device.ui.atomic.molecule.SwipeToDeleteContainer
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

@Composable
fun ImageGrid(
    images: List<AttachmentVS>,
    newImages: List<MemImgVS>,
    deleteNew: ((MemImgVS) -> Unit)? = null,
    delete: ((Int) -> Unit)? = null,
    select: ((Int) -> Unit)? = null
) {
    var showRemoveAlertForId by remember { mutableStateOf(-1) }
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

    if (showRemoveAlertForId != -1) {
        OrganismAlertDialog(
            title = stringResource(Res.string.deleteing_image),
            text = stringResource(Res.string.confirm_deleteing_image),
            ok = {
                delete?.invoke(showRemoveAlertForId)
                showRemoveAlertForId = -1
            },
            cancel = {
                showRemoveAlertForId = -1
            }
        )
    }

    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        images.forEach { (key, imageUrl) ->
            SwipeToDeleteContainer(
                modifier = Modifier.size(84.dp),
                onDelete = delete?.let {
                    {
                        showRemoveAlertForId = key
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
                            select?.invoke(key)
                        },
                        resource = asyncPainterResource(data = imageUrl),
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