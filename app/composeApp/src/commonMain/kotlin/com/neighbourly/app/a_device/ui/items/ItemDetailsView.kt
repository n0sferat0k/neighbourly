@file:OptIn(ExperimentalLayoutApi::class)

package com.neighbourly.app.a_device.ui.items

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.wrapContentWidth
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.darkrockstudios.libraries.mpfilepicker.FilePicker
import com.neighbourly.app.KoinProvider
import com.neighbourly.app.a_device.ui.AppColors
import com.neighbourly.app.a_device.ui.atomic.atom.FriendlyButton
import com.neighbourly.app.a_device.ui.atomic.atom.FriendlyErrorText
import com.neighbourly.app.a_device.ui.atomic.atom.FriendlyText
import com.neighbourly.app.a_device.ui.atomic.molecule.AutocompleteOutlinedTextField
import com.neighbourly.app.a_device.ui.atomic.molecule.CardFooter
import com.neighbourly.app.a_device.ui.atomic.molecule.CardHeader
import com.neighbourly.app.a_device.ui.atomic.molecule.CardScrollableContent
import com.neighbourly.app.a_device.ui.atomic.molecule.SwipeToDeleteContainer
import com.neighbourly.app.a_device.ui.datetime.DateTimeDialog
import com.neighbourly.app.a_device.ui.utils.AlertDialog
import com.neighbourly.app.b_adapt.viewmodel.items.ItemDetailsViewModel
import com.neighbourly.app.b_adapt.viewmodel.items.ItemDetailsViewModel.MemImg
import com.neighbourly.app.b_adapt.viewmodel.navigation.NavigationViewModel
import com.neighbourly.app.d_entity.data.ItemType.BARTER
import com.neighbourly.app.d_entity.data.ItemType.DONATION
import com.neighbourly.app.d_entity.data.ItemType.EVENT
import com.neighbourly.app.d_entity.data.ItemType.NEED
import com.neighbourly.app.d_entity.data.ItemType.REMINDER
import com.neighbourly.app.d_entity.data.ItemType.REQUEST
import com.neighbourly.app.d_entity.data.ItemType.SALE
import com.neighbourly.app.d_entity.data.ItemType.SKILLSHARE
import com.neighbourly.app.loadImageFromFile
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlinx.datetime.Clock.System.now
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.add_date
import neighbourly.composeapp.generated.resources.add_end
import neighbourly.composeapp.generated.resources.add_file
import neighbourly.composeapp.generated.resources.add_image
import neighbourly.composeapp.generated.resources.add_start
import neighbourly.composeapp.generated.resources.barter
import neighbourly.composeapp.generated.resources.bartering
import neighbourly.composeapp.generated.resources.confirm_deleteing_file
import neighbourly.composeapp.generated.resources.confirm_deleteing_image
import neighbourly.composeapp.generated.resources.confirm_deleteing_this_item
import neighbourly.composeapp.generated.resources.confirm_new_file
import neighbourly.composeapp.generated.resources.confirm_new_image
import neighbourly.composeapp.generated.resources.dates
import neighbourly.composeapp.generated.resources.delete
import neighbourly.composeapp.generated.resources.deleteing_file
import neighbourly.composeapp.generated.resources.deleteing_image
import neighbourly.composeapp.generated.resources.deleteing_item
import neighbourly.composeapp.generated.resources.donate
import neighbourly.composeapp.generated.resources.donation
import neighbourly.composeapp.generated.resources.end_date
import neighbourly.composeapp.generated.resources.event
import neighbourly.composeapp.generated.resources.files
import neighbourly.composeapp.generated.resources.images
import neighbourly.composeapp.generated.resources.item_description
import neighbourly.composeapp.generated.resources.item_name
import neighbourly.composeapp.generated.resources.item_url
import neighbourly.composeapp.generated.resources.need
import neighbourly.composeapp.generated.resources.new_file
import neighbourly.composeapp.generated.resources.new_image
import neighbourly.composeapp.generated.resources.newbadge
import neighbourly.composeapp.generated.resources.reminder
import neighbourly.composeapp.generated.resources.reminders
import neighbourly.composeapp.generated.resources.request
import neighbourly.composeapp.generated.resources.sale
import neighbourly.composeapp.generated.resources.save
import neighbourly.composeapp.generated.resources.skillshare
import neighbourly.composeapp.generated.resources.start_date
import neighbourly.composeapp.generated.resources.target_user
import neighbourly.composeapp.generated.resources.type
import neighbourly.composeapp.generated.resources.unknown
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import java.time.format.DateTimeFormatter

val TYPE_ASSOC = mapOf(
    DONATION.name to Pair(Res.drawable.donate, Res.string.donation),
    BARTER.name to Pair(Res.drawable.barter, Res.string.bartering),
    SALE.name to Pair(Res.drawable.sale, Res.string.sale),
    EVENT.name to Pair(Res.drawable.event, Res.string.event),
    NEED.name to Pair(Res.drawable.need, Res.string.need),
    REQUEST.name to Pair(Res.drawable.request, Res.string.request),
    SKILLSHARE.name to Pair(Res.drawable.skillshare, Res.string.skillshare),
)
val TYPE_ASSOC_ADMIN = TYPE_ASSOC + mapOf(
    REMINDER.name to Pair(Res.drawable.reminder, Res.string.reminders),
)

val LOCALLY_ALLOWED_SITES =
    listOf("youtube.com", "youtu.be", "facebook", "pinterest", "goo.gl/photos")

@Composable
fun ItemDetailsView(
    itemId: Int? = null,
    viewModel: ItemDetailsViewModel = viewModel { KoinProvider.KOIN.get<ItemDetailsViewModel>() },
    navigationViewModel: NavigationViewModel = viewModel { KoinProvider.KOIN.get<NavigationViewModel>() }
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(itemId) {
        viewModel.setItem(itemId)
    }

    LaunchedEffect(state.deleted) {
        if (state.deleted) {
            viewModel.deleteItemAck()
            navigationViewModel.goBack()
        }
    }

    if (state.editable) {
        EditableItemDetailsView(viewModel, navigationViewModel, state)
    } else {
        StaticItemDetailsView(state, navigationViewModel)
    }
}

@Composable
fun StaticItemDetailsView(
    state: ItemDetailsViewModel.ItemDetailsViewState,
    navigationViewModel: NavigationViewModel
) {
    val uriHandler = LocalUriHandler.current
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        CardHeader(Modifier.align(Alignment.Start))

        CardScrollableContent(modifier = Modifier.weight(1f)) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    FriendlyText(text = stringResource(Res.string.type), bold = true)
                    FriendlyText(
                        text = stringResource(
                            TYPE_ASSOC.get(state.type)?.second ?: Res.string.unknown
                        ),
                    )
                    TypeOption(
                        icon = painterResource(
                            TYPE_ASSOC.get(state.type)?.first ?: Res.drawable.newbadge
                        ),
                        selected = true,
                        contentDesc = state.type,
                    ) {}
                }

                if (listOf(NEED.name, REQUEST.name).contains(state.type)
                    && state.targetUserId != null
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        FriendlyText(text = stringResource(Res.string.target_user), bold = true)
                        FriendlyText(text = state.users.getOrDefault(state.targetUserId, ""))
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    FriendlyText(text = stringResource(Res.string.item_name), bold = true)
                    FriendlyText(text = state.name)
                }
                if (state.description.isNotEmpty()) {
                    FriendlyText(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(Res.string.item_description), bold = true
                    )
                    FriendlyText(
                        modifier = Modifier.fillMaxWidth(),
                        text = state.description
                    )
                }

                if (state.url.isNotEmpty()) {
                    FriendlyText(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(Res.string.item_url), bold = true
                    )
                    FriendlyText(modifier = Modifier.clickable {
                        if (LOCALLY_ALLOWED_SITES.any { state.url.contains(it) }) {
                            navigationViewModel.goToWebPage(state.url)
                        } else {
                            uriHandler.openUri(state.url)
                        }
                    }, text = state.url)
                }

                if (!state.images.isEmpty()) {
                    FriendlyText(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(Res.string.images),
                        bold = true
                    )

                    if (state.images.size > 0 || state.newImages.size > 0) {
                        ImageGrid(
                            images = state.images,
                            newImages = state.newImages
                        ) { imageId ->
                            state.itemId?.let { navigationViewModel.goToGallery(it, imageId) }
                        }
                    }
                }

                if (!state.files.isEmpty()) {
                    FriendlyText(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(Res.string.files),
                        bold = true
                    )

                    state.files.onEach {
                        FriendlyText(
                            modifier = Modifier.fillMaxWidth()
                                .clickable {
                                    uriHandler.openUri(it.url)
                                },
                            text = it.name, bold = true
                        )
                    }
                }

                if (state.start != null && state.start.epochSeconds > 0) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        FriendlyText(text = stringResource(Res.string.start_date))
                        FriendlyText(
                            text = state.start.toLocalDateTime(TimeZone.currentSystemDefault())
                                .toJavaLocalDateTime().format(formatter),
                            bold = true
                        )
                    }
                }

                if (state.end != null && state.end.epochSeconds > 0) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        FriendlyText(text = stringResource(Res.string.end_date))
                        FriendlyText(
                            text = state.end.toLocalDateTime(TimeZone.currentSystemDefault())
                                .toJavaLocalDateTime().format(formatter),
                            bold = true
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EditableItemDetailsView(
    viewModel: ItemDetailsViewModel,
    navigationViewModel: NavigationViewModel,
    state: ItemDetailsViewModel.ItemDetailsViewState
) {
    var showImageFilePicker by remember { mutableStateOf(false) }
    var showAttachmentFilePicker by remember { mutableStateOf(false) }
    var showNewFileAlert by remember { mutableStateOf(false) }
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    var showDatePickerInstant by remember { mutableStateOf<Instant?>(null) }
    var showDatePickerIndex by remember { mutableStateOf(-1) }
    var showDeleteAlert by remember { mutableStateOf(false) }
    var showRemoveAlertForFileId by remember { mutableStateOf(-1) }
    val uriHandler = LocalUriHandler.current
    val focusManager = LocalFocusManager.current
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    val badge = painterResource(Res.drawable.newbadge)

    if (showRemoveAlertForFileId != -1) {
        AlertDialog(
            title = stringResource(Res.string.deleteing_file),
            text = stringResource(Res.string.confirm_deleteing_file),
            ok = {
                viewModel.deleteFile(showRemoveAlertForFileId)
                showRemoveAlertForFileId = -1
            },
            cancel = {
                showRemoveAlertForFileId = -1
            }
        )
    }

    if (showDeleteAlert) {
        AlertDialog(
            title = stringResource(Res.string.deleteing_item),
            text = stringResource(Res.string.confirm_deleteing_this_item),
            ok = {
                showDeleteAlert = false
                viewModel.deleteItem()
            },
            cancel = {
                showDeleteAlert = false
            }
        )
    }

    if (showNewFileAlert) {
        AlertDialog(
            title = stringResource(Res.string.new_file),
            text = stringResource(Res.string.confirm_new_file),
            ok = {
                showNewFileAlert = false
            },
        )
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

    FilePicker(
        show = showAttachmentFilePicker,
        fileExtensions = listOf(
            "jpg",
            "png",
            "pdf",
            "txt",
            "svg",
            "doc",
            "docx",
            "ppt",
            "pptx",
            "*"
        )
    ) { file ->
        showAttachmentFilePicker = false

        file?.platformFile?.toString()?.let {
            viewModel.onAddFile(it)
        }
    }

    if (showStartDatePicker) {
        DateTimeDialog(
            title = stringResource(Res.string.start_date),
            instant = state.startOverride ?: state.start ?: now()
        ) {
            it?.let { viewModel.updateStartDate(it) }
            showStartDatePicker = false
        }
    }
    if (showEndDatePicker) {
        DateTimeDialog(
            title = stringResource(Res.string.end_date),
            instant = state.endOverride ?: state.end ?: now()
        ) {
            it?.let { viewModel.updateEndDate(it) }
            showEndDatePicker = false
        }
    }
    if (showDatePickerInstant != null) {
        DateTimeDialog(
            title = stringResource(Res.string.reminders),
            instant = showDatePickerInstant ?: now()
        ) {
            it?.let { viewModel.addOrUpdateDate(it, showDatePickerIndex) }
            showDatePickerInstant = null
        }
    }
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        CardHeader(Modifier.align(Alignment.Start))

        CardScrollableContent(modifier = Modifier.weight(1f)) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    FriendlyText(text = stringResource(Res.string.type))

                    FriendlyText(
                        text = stringResource(
                            TYPE_ASSOC_ADMIN.get(state.typeOverride ?: state.type)?.second
                                ?: Res.string.unknown
                        ),
                        bold = true,
                    )
                }

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalArrangement = Arrangement.Start,
                ) {
                    (if (state.admin) TYPE_ASSOC_ADMIN else TYPE_ASSOC).forEach { (typeId, iconNamePair) ->
                        TypeOption(
                            icon = painterResource(iconNamePair.first),
                            selected = (state.typeOverride ?: state.type) == typeId,
                            contentDesc = typeId,
                        ) {
                            viewModel.setType(typeId)
                        }
                    }
                }

                AnimatedVisibility(
                    listOf(NEED.name, REQUEST.name).contains(
                        state.typeOverride ?: state.type
                    )
                ) {
                    AutocompleteOutlinedTextField(
                        text = (state.targetUserId ?: state.targetUserIdOverride)?.let { userId ->
                            state.users.getOrDefault(userId, "")
                        } ?: "",
                        label = { Text(stringResource(Res.string.target_user)) },
                        entries = state.users,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        viewModel.setTargetUser(it)
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

                AnimatedVisibility((state.typeOverride ?: state.type) != REMINDER.name) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = state.descriptionOverride ?: state.description,
                            onValueChange = {
                                viewModel.updateDescription(it)
                            },
                            maxLines = 5,
                            label = { Text(stringResource(Res.string.item_description)) },
                            modifier = Modifier.fillMaxWidth(),
                        )

                        OutlinedTextField(
                            value = state.urlOverride ?: state.url,
                            onValueChange = {
                                viewModel.updateUrl(it)
                            },
                            isError = state.urlError,
                            label = { Text(stringResource(Res.string.item_url)) },
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
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
                    ) { imageId ->
                        state.itemId?.let { navigationViewModel.goToGallery(it, imageId) }
                    }
                }

                AnimatedVisibility((state.typeOverride ?: state.type) == REMINDER.name) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            FriendlyText(text = stringResource(Res.string.dates))
                            FriendlyText(modifier = Modifier.clickable {
                                showDatePickerIndex = -1
                                showDatePickerInstant = state.dates.lastOrNull() ?: now()
                            }, text = stringResource(Res.string.add_date), bold = true)
                        }

                        state.dates.forEachIndexed { index, date ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                FriendlyText(
                                    modifier = Modifier.clickable {
                                        showDatePickerIndex = index
                                        showDatePickerInstant = date
                                    },
                                    text = date.toLocalDateTime(TimeZone.currentSystemDefault())
                                        .toJavaLocalDateTime().format(formatter),
                                    bold = true
                                )
                                FriendlyText(
                                    modifier = Modifier.clickable {
                                        viewModel.addOrUpdateDate(null, index)
                                    },
                                    text = stringResource(Res.string.delete),
                                    bold = true
                                )
                            }
                        }
                    }
                }

                AnimatedVisibility((state.typeOverride ?: state.type) != REMINDER.name) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            FriendlyText(text = stringResource(Res.string.files))
                            FriendlyText(modifier = Modifier.clickable {
                                focusManager.clearFocus(true)
                                showAttachmentFilePicker = true
                            }, text = stringResource(Res.string.add_file), bold = true)
                        }

                        state.files.onEach {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                FriendlyText(
                                    modifier = Modifier.weight(1f).clickable {
                                        uriHandler.openUri(it.url)
                                    }, text = it.name, bold = true
                                )

                                FriendlyText(
                                    modifier = Modifier.wrapContentWidth().clickable {
                                        showRemoveAlertForFileId = it.id
                                    },
                                    text = stringResource(Res.string.delete),
                                    bold = true
                                )
                            }
                        }

                        state.newFiles.onEach {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                FriendlyText(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            showNewFileAlert = true
                                        }, text = it.value, bold = true
                                )
                                Image(
                                    modifier = Modifier.size(36.dp),
                                    painter = badge,
                                    contentScale = ContentScale.FillBounds,
                                    contentDescription = "Item File New Badge",
                                    colorFilter = ColorFilter.tint(AppColors.primary),
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            FriendlyText(text = stringResource(Res.string.start_date))
                            (state.startOverride ?: state.start)?.takeIf { it.epochSeconds > 0 }
                                .let { startInstance ->
                                    if (startInstance != null) {
                                        FriendlyText(
                                            modifier = Modifier.clickable {
                                                focusManager.clearFocus(true)
                                                showStartDatePicker = true
                                            },
                                            text = startInstance.toLocalDateTime(TimeZone.currentSystemDefault())
                                                .toJavaLocalDateTime().format(formatter),
                                            bold = true
                                        )
                                    }
                                    FriendlyText(
                                        modifier = Modifier.clickable {
                                            if (startInstance == null) {
                                                focusManager.clearFocus(true)
                                                showStartDatePicker = true
                                            } else
                                                viewModel.updateStartDate(null)
                                        },
                                        text = if (startInstance == null)
                                            stringResource(Res.string.add_start)
                                        else
                                            stringResource(Res.string.delete),
                                        bold = true
                                    )
                                }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            FriendlyText(text = stringResource(Res.string.end_date))
                            (state.endOverride ?: state.end)?.takeIf { it.epochSeconds > 0 }
                                .let { endInstance ->
                                    if (endInstance != null) {
                                        FriendlyText(
                                            modifier = Modifier.clickable {
                                                focusManager.clearFocus(true)
                                                showEndDatePicker = true
                                            },
                                            text = endInstance.toLocalDateTime(TimeZone.currentSystemDefault())
                                                .toJavaLocalDateTime().format(formatter),
                                            bold = true
                                        )
                                    }
                                    FriendlyText(
                                        modifier = Modifier.clickable {
                                            if (endInstance == null) {
                                                focusManager.clearFocus(true)
                                                showEndDatePicker = true
                                            } else
                                                viewModel.updateEndDate(null)
                                        },
                                        text = if (endInstance == null)
                                            stringResource(Res.string.add_end)
                                        else
                                            stringResource(Res.string.delete),
                                        bold = true
                                    )
                                }
                        }
                    }
                }

                if (state.hasChanged) {
                    FriendlyButton(
                        text = stringResource(Res.string.save),
                        loading = state.saving,
                    ) {
                        viewModel.save()
                    }
                }

                if (state.error.isNotEmpty()) {
                    FriendlyErrorText(state.error)
                }
            }
        }
        CardFooter {
            if (state.editable && state.itemId != null) {
                FriendlyText(
                    modifier = Modifier.clickable {
                        showDeleteAlert = true
                    },
                    text = stringResource(Res.string.delete), bold = true
                )
            }
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
    images: List<ItemDetailsViewModel.AttachmentVS>,
    newImages: List<MemImg>,
    deleteNew: ((MemImg) -> Unit)? = null,
    delete: ((Int) -> Unit)? = null,
    select: ((Int) -> Unit)? = null
) {
    var showRemoveAlertForId by remember { mutableStateOf(-1) }
    var showNewImageAlert by remember { mutableStateOf(false) }
    val badge = painterResource(Res.drawable.newbadge)

    if (showNewImageAlert) {
        AlertDialog(
            title = stringResource(Res.string.new_image),
            text = stringResource(Res.string.confirm_new_image),
            ok = {
                showNewImageAlert = false
            },
        )
    }

    if (showRemoveAlertForId != -1) {
        AlertDialog(
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