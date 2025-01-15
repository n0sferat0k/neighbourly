package com.neighbourly.app.a_device.ui.atomic.organism.item

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import com.darkrockstudios.libraries.mpfilepicker.FilePicker
import com.neighbourly.app.a_device.ui.AppColors
import com.neighbourly.app.a_device.ui.atomic.atom.FriendlyButton
import com.neighbourly.app.a_device.ui.atomic.atom.FriendlyErrorText
import com.neighbourly.app.a_device.ui.atomic.atom.FriendlyText
import com.neighbourly.app.a_device.ui.atomic.molecule.AutocompleteOutlinedTextField
import com.neighbourly.app.a_device.ui.atomic.molecule.card.CardFooter
import com.neighbourly.app.a_device.ui.atomic.molecule.card.CardHeader
import com.neighbourly.app.a_device.ui.atomic.molecule.card.CardScrollableContent
import com.neighbourly.app.a_device.ui.atomic.molecule.item.ImageGrid
import com.neighbourly.app.a_device.ui.atomic.molecule.item.ItemTypeOption
import com.neighbourly.app.a_device.ui.atomic.organism.datetime.OrganismDateTimeDialog
import com.neighbourly.app.a_device.ui.atomic.organism.util.OrganismAlertDialog
import com.neighbourly.app.a_device.ui.atomic.page.TYPE_ASSOC
import com.neighbourly.app.a_device.ui.atomic.page.TYPE_ASSOC_ADMIN
import com.neighbourly.app.b_adapt.viewmodel.bean.ItemTypeVS
import com.neighbourly.app.b_adapt.viewmodel.bean.ItemTypeVS.*
import com.neighbourly.app.b_adapt.viewmodel.bean.ItemVS
import com.neighbourly.app.loadImageFromFile
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.Instant.Companion.fromEpochSeconds
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.add_date
import neighbourly.composeapp.generated.resources.add_end
import neighbourly.composeapp.generated.resources.add_file
import neighbourly.composeapp.generated.resources.add_image
import neighbourly.composeapp.generated.resources.add_start
import neighbourly.composeapp.generated.resources.confirm_deleteing_file
import neighbourly.composeapp.generated.resources.confirm_deleteing_this_item
import neighbourly.composeapp.generated.resources.confirm_new_file
import neighbourly.composeapp.generated.resources.dates
import neighbourly.composeapp.generated.resources.delete
import neighbourly.composeapp.generated.resources.deleteing_file
import neighbourly.composeapp.generated.resources.deleteing_item
import neighbourly.composeapp.generated.resources.end_date
import neighbourly.composeapp.generated.resources.files
import neighbourly.composeapp.generated.resources.images
import neighbourly.composeapp.generated.resources.item_description
import neighbourly.composeapp.generated.resources.item_name
import neighbourly.composeapp.generated.resources.item_url
import neighbourly.composeapp.generated.resources.new_file
import neighbourly.composeapp.generated.resources.newbadge
import neighbourly.composeapp.generated.resources.reminders
import neighbourly.composeapp.generated.resources.save
import neighbourly.composeapp.generated.resources.start_date
import neighbourly.composeapp.generated.resources.target_user
import neighbourly.composeapp.generated.resources.type
import neighbourly.composeapp.generated.resources.unknown
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OrganismEditableItemDetails(
    //state: ItemDetailsViewModel.ItemDetailsViewState
    item: ItemVS,
    users: Map<Int, String>,
    deleteFile: (fileId: Int) -> Unit,
    deleteItem: () -> Unit,
    onAddImage: (url: String, img: BitmapPainter) -> Unit,
    onAddFile: (url: String) -> Unit,
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

    var targetUserIdOverride by remember { mutableStateOf<Int?>(null) }
    var startOverride by remember { mutableStateOf<Instant?>(null) }
    var endOverride by remember { mutableStateOf<Instant?>(null) }
    var typeOverride by remember { mutableStateOf<ItemTypeVS?>(null) }
    var nameOverride by remember { mutableStateOf<String?>(null) }
    var descriptionOverride by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(state.deleted) {
        if (state.deleted) {
            viewModel.deleteItemAck()
            navigationViewModel.goBack()
        }
    }

    if (showRemoveAlertForFileId != -1) {
        OrganismAlertDialog(
            title = stringResource(Res.string.deleteing_file),
            text = stringResource(Res.string.confirm_deleteing_file),
            ok = {
                deleteFile(showRemoveAlertForFileId)
                showRemoveAlertForFileId = -1
            },
            cancel = {
                showRemoveAlertForFileId = -1
            }
        )
    }

    if (showDeleteAlert) {
        OrganismAlertDialog(
            title = stringResource(Res.string.deleteing_item),
            text = stringResource(Res.string.confirm_deleteing_this_item),
            ok = {
                showDeleteAlert = false
                deleteItem()
            },
            cancel = {
                showDeleteAlert = false
            }
        )
    }

    if (showNewFileAlert) {
        OrganismAlertDialog(
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
                onAddImage(it, img)
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
            onAddFile(it)
        }
    }

    if (showStartDatePicker) {
        OrganismDateTimeDialog(
            title = stringResource(Res.string.start_date),
            instant = startOverride ?: item.start ?: Clock.System.now()
        ) {
            it?.let {
                startOverride = fromEpochSeconds(it.toLong())
            }
            showStartDatePicker = false
        }
    }
    if (showEndDatePicker) {
        OrganismDateTimeDialog(
            title = stringResource(Res.string.end_date),
            instant = endOverride ?: item.end ?: Clock.System.now()
        ) {
            it?.let {
                endOverride = fromEpochSeconds(it.toLong())
            }
            showEndDatePicker = false
        }
    }
    if (showDatePickerInstant != null) {
        OrganismDateTimeDialog(
            title = stringResource(Res.string.reminders),
            instant = showDatePickerInstant ?: Clock.System.now()
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
                            TYPE_ASSOC_ADMIN.get(typeOverride ?: item.type)?.second
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
                    (if (state.admin) TYPE_ASSOC_ADMIN else TYPE_ASSOC).forEach { (type, iconNamePair) ->
                        ItemTypeOption(
                            icon = painterResource(iconNamePair.first),
                            selected = (typeOverride ?: item.type) == type,
                            contentDesc = type.name,
                        ) {
                            typeOverride = type
                            if (!listOf(NEED, REQUEST).contains(type)) {
                                targetUserIdOverride = -1
                            }
                        }
                    }
                }

                AnimatedVisibility(listOf(NEED, REQUEST).contains(typeOverride ?: item.type)) {
                    AutocompleteOutlinedTextField(
                        text = (item.targetUserId ?: targetUserIdOverride)?.let { userId ->
                            users.getOrDefault(userId, "")
                        } ?: "",
                        label = { Text(stringResource(Res.string.target_user)) },
                        entries = users,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        targetUserIdOverride = it
                    }
                }

                OutlinedTextField(
                    value = nameOverride ?: item.name,
                    onValueChange = {
                        nameOverride =  it
                        //todo fix error show on name format
                        //todo fix hasChanged mechanics 
                        viewModel.updateName(it)
                    },
                    isError = state.nameError,
                    label = { Text(stringResource(Res.string.item_name)) },
                    modifier = Modifier.fillMaxWidth(),
                )

                AnimatedVisibility((typeOverride ?: item.type) != ItemType.REMINDER.name) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = descriptionOverride ?: item.description,
                            onValueChange = {
                                descriptionOverride = it
                            },
                            maxLines = 5,
                            label = { Text(stringResource(Res.string.item_description)) },
                            modifier = Modifier.fillMaxWidth(),
                        )

                        OutlinedTextField(
                            value = state.urlOverride ?: item.url,
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

                if (item.images.size > 0 || state.newImages.size > 0) {
                    ImageGrid(
                        images = item.images,
                        newImages = state.newImages,
                        deleteNew = {
                            viewModel.deleteNewImage(it.name)
                        },
                        delete = {
                            viewModel.deleteImage(it)
                        }
                    ) { imageId ->
                        item.id?.let { navigationViewModel.goToGallery(it, imageId) }
                    }
                }

                AnimatedVisibility((typeOverride ?: item.type) == ItemType.REMINDER.name) {
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
                                showDatePickerInstant =
                                    state.dates.lastOrNull() ?: Clock.System.now()
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

                AnimatedVisibility((typeOverride ?: item.type) != ItemType.REMINDER.name) {
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

                        item.files.onEach {
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
                            (startOverride ?: item.start)?.takeIf { it.epochSeconds > 0 }
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
                                            } else {
                                                startOverride = fromEpochSeconds(0)
                                            }
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
                            (endOverride ?: item.end)?.takeIf { it.epochSeconds > 0 }
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
                                            } else {
                                                endOverride = fromEpochSeconds(0)
                                            }
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
            if (state.editable && item.id != null) {
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