@file:OptIn(ExperimentalLayoutApi::class)

package com.neighbourly.app.a_device.ui.items

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.darkrockstudios.libraries.mpfilepicker.FilePicker
import com.neighbourly.app.KoinProvider
import com.neighbourly.app.a_device.ui.Alert
import com.neighbourly.app.a_device.ui.AppColors
import com.neighbourly.app.a_device.ui.AutocompleteOutlinedTextField
import com.neighbourly.app.a_device.ui.BoxFooter
import com.neighbourly.app.a_device.ui.BoxHeader
import com.neighbourly.app.a_device.ui.BoxScrollableContent
import com.neighbourly.app.a_device.ui.CurlyButton
import com.neighbourly.app.a_device.ui.CurlyText
import com.neighbourly.app.a_device.ui.SwipeToDeleteBox
import com.neighbourly.app.b_adapt.viewmodel.items.ItemDetailsViewModel
import com.neighbourly.app.b_adapt.viewmodel.items.ItemDetailsViewModel.MemImg
import com.neighbourly.app.b_adapt.viewmodel.navigation.NavigationViewModel
import com.neighbourly.app.d_entity.data.ItemType.BARTER
import com.neighbourly.app.d_entity.data.ItemType.DONATION
import com.neighbourly.app.d_entity.data.ItemType.EVENT
import com.neighbourly.app.d_entity.data.ItemType.NEED
import com.neighbourly.app.d_entity.data.ItemType.REQUEST
import com.neighbourly.app.d_entity.data.ItemType.SALE
import com.neighbourly.app.d_entity.data.ItemType.SKILLSHARE
import com.neighbourly.app.loadImageFromFile
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.add_file
import neighbourly.composeapp.generated.resources.add_image
import neighbourly.composeapp.generated.resources.barter
import neighbourly.composeapp.generated.resources.bartering
import neighbourly.composeapp.generated.resources.confirm_deleteing_image
import neighbourly.composeapp.generated.resources.delete
import neighbourly.composeapp.generated.resources.deleteing_image
import neighbourly.composeapp.generated.resources.donate
import neighbourly.composeapp.generated.resources.donation
import neighbourly.composeapp.generated.resources.event
import neighbourly.composeapp.generated.resources.files
import neighbourly.composeapp.generated.resources.images
import neighbourly.composeapp.generated.resources.item_description
import neighbourly.composeapp.generated.resources.item_name
import neighbourly.composeapp.generated.resources.item_url
import neighbourly.composeapp.generated.resources.need
import neighbourly.composeapp.generated.resources.request
import neighbourly.composeapp.generated.resources.sale
import neighbourly.composeapp.generated.resources.save
import neighbourly.composeapp.generated.resources.skillshare
import neighbourly.composeapp.generated.resources.target_user
import neighbourly.composeapp.generated.resources.type
import neighbourly.composeapp.generated.resources.unknown
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun ItemDetailsView(
    itemId: Int? = null,
    viewModel: ItemDetailsViewModel = viewModel { KoinProvider.KOIN.get<ItemDetailsViewModel>() },
    navigationViewModel: NavigationViewModel = viewModel { KoinProvider.KOIN.get<NavigationViewModel>() }
) {
    val state by viewModel.state.collectAsState()
    val navigation by navigationViewModel.state.collectAsState()
    var showImageFilePicker by remember { mutableStateOf(false) }
    var showAttachmentFilePicker by remember { mutableStateOf(false) }
    val uriHandler = LocalUriHandler.current

    LaunchedEffect(itemId) {
        viewModel.setItem(itemId)
    }

    FilePicker(show = showImageFilePicker, fileExtensions = listOf("jpg", "png")) { file ->
        showImageFilePicker = false

        file?.platformFile?.toString()?.let {
            val img = loadImageFromFile(it, 400)
            if (img != null) {
                viewModel.onAddImage(it, img)
            }
        }
    }

    FilePicker(show = showAttachmentFilePicker, fileExtensions = listOf("jpg", "png", "pdf", "txt", "svg", "doc", "docx", "ppt", "pptx", "*")) { file ->
        showAttachmentFilePicker = false

        file?.platformFile?.toString()?.let {
            viewModel.onAddFile(it)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        BoxHeader(Modifier.align(Alignment.Start))

        BoxScrollableContent(modifier = Modifier.weight(1f)) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    CurlyText(text = stringResource(Res.string.type))

                    CurlyText(
                        text = stringResource(
                            when (state.typeOverride ?: state.type) {
                                DONATION.name -> Res.string.donation
                                BARTER.name -> Res.string.bartering
                                SALE.name -> Res.string.sale
                                EVENT.name -> Res.string.event
                                NEED.name -> Res.string.need
                                REQUEST.name -> Res.string.request
                                SKILLSHARE.name -> Res.string.skillshare
                                else -> {
                                    Res.string.unknown
                                }
                            }
                        ),
                        bold = true,
                    )
                }

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalArrangement = Arrangement.Start,
                ) {
                    mapOf(
                        DONATION.name to painterResource(Res.drawable.donate),
                        BARTER.name to painterResource(Res.drawable.barter),
                        SALE.name to painterResource(Res.drawable.sale),
                        EVENT.name to painterResource(Res.drawable.event),
                        NEED.name to painterResource(Res.drawable.need),
                        REQUEST.name to painterResource(Res.drawable.request),
                        SKILLSHARE.name to painterResource(Res.drawable.skillshare)
                    ).forEach { (typeId, icon) ->
                        TypeOption(
                            icon = icon,
                            selected = (state.typeOverride ?: state.type) == typeId,
                            contentDesc = typeId,
                        ) {
                            viewModel.setType(typeId)
                        }
                    }
                }

                OutlinedTextField(
                    value = state.nameOverride ?: state.name,
                    onValueChange = {
                        viewModel.updateName(it)
                    },
                    isError = state.nameError,
                    label = { Text(stringResource(Res.string.item_name)) },
                    modifier = Modifier.fillMaxWidth(),
                )

                OutlinedTextField(
                    value = state.descriptionOverride ?: state.description,
                    onValueChange = {
                        viewModel.updateDescription(it)
                    },
                    maxLines = 5,
                    label = { Text(stringResource(Res.string.item_description)) },
                    modifier = Modifier.fillMaxWidth(),
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    CurlyText(text = stringResource(Res.string.images))
                    CurlyText(modifier = Modifier.clickable {
                        showImageFilePicker = true
                    }, text = stringResource(Res.string.add_image), bold = true)
                }

                if (state.images.size > 0 || state.newImages.size > 0) {
                    ImageGrid(
                        images = state.images,
                        newImages = state.newImages,
                        deleteNew = {
                            viewModel.deleteNewImage(it.name)
                        },
                        delete = {
                            viewModel.deleteImage(it)
                        }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    CurlyText(text = stringResource(Res.string.files))
                    CurlyText(modifier = Modifier.clickable {
                        showAttachmentFilePicker = true
                    }, text = stringResource(Res.string.add_file), bold = true)
                }

                state.files.onEach {
                    CurlyText(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                uriHandler.openUri(it.url)
                            }, text = it.name, bold = true
                    )
                }

                AutocompleteOutlinedTextField(
                    label = { Text(stringResource(Res.string.target_user)) },
                    entries = state.users,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    viewModel.setTargetUser(it)
                }

                OutlinedTextField(
                    value = state.urlOverride ?: state.url,
                    onValueChange = {
                        viewModel.updateUrl(it)
                    },
                    isError = state.nameError,
                    label = { Text(stringResource(Res.string.item_url)) },
                    modifier = Modifier.fillMaxWidth(),
                )

                CurlyButton(
                    text = stringResource(Res.string.save),
                    loading = state.saving,
                ) {
                    viewModel.onSave()
                }
            }
        }
        BoxFooter(modifier = Modifier.align(Alignment.End)) {
            CurlyText(text = stringResource(Res.string.delete), bold = true)
        }
    }
}

@Composable
fun TypeOption(selected: Boolean = false, icon: Painter, contentDesc: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier.size(36.dp).let {
            if (selected) {
                it.border(2.dp, AppColors.primary, CircleShape)
            } else it
        },
        contentAlignment = Alignment.Center,
    ) {
        Image(
            modifier =
            Modifier.size(30.dp).clickable {
                onClick()
            },
            painter = icon,
            contentDescription = "Type Image",
            colorFilter = ColorFilter.tint(AppColors.primary),
        )
    }

}

@Composable
fun ImageGrid(
    images: Map<Int, String>,
    newImages: List<MemImg>,
    deleteNew: (MemImg) -> Unit,
    delete: (Int) -> Unit
) {
    var showRemoveAlertForId by remember { mutableStateOf(-1) }

    if (showRemoveAlertForId != -1) {
        Alert(
            title = stringResource(Res.string.deleteing_image),
            text = stringResource(Res.string.confirm_deleteing_image),
            ok = {
                showRemoveAlertForId = -1
                delete(showRemoveAlertForId)
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
            SwipeToDeleteBox(modifier = Modifier.size(84.dp), onDelete = {
                showRemoveAlertForId = key
            }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f),
                    shape = RoundedCornerShape(4.dp),
                    elevation = 4.dp
                ) {
                    KamelImage(
                        modifier = Modifier.fillMaxSize(),
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
            SwipeToDeleteBox(modifier = Modifier.size(84.dp), onDelete = {
                deleteNew(memImg)
            }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f),
                    shape = RoundedCornerShape(4.dp),
                    elevation = 4.dp
                ) {
                    Image(
                        modifier = Modifier.fillMaxSize(),
                        painter = memImg.img,
                        contentDescription = "Item Image",
                        contentScale = ContentScale.Crop,
                    )
                }
            }
        }
    }
}