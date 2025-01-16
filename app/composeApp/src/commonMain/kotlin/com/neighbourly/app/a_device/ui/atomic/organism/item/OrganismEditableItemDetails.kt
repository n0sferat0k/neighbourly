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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
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
import com.neighbourly.app.b_adapt.viewmodel.bean.ItemTypeVS.NEED
import com.neighbourly.app.b_adapt.viewmodel.bean.ItemTypeVS.REMINDER
import com.neighbourly.app.b_adapt.viewmodel.bean.ItemTypeVS.REQUEST
import com.neighbourly.app.b_adapt.viewmodel.bean.ItemVS
import com.neighbourly.app.b_adapt.viewmodel.bean.MemImgVS
import com.neighbourly.app.d_entity.util.isValidUrl
import com.neighbourly.app.loadImageFromFile
import com.neighbourly.app.loadNameFromFile
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
    item: ItemVS,
    users: Map<Int, String>,
    isAdmin: Boolean,
    error: String,
    saving: Boolean,
    onSave: (
        typeOverride: ItemTypeVS?,
        nameOverride: String?,
        descriptionOverride: String?,
        datesOverride: List<Instant>?,
        targetUserIdOverride: Int?,
        urlOverride: String?,
        startOverride: Instant?,
        endOverride: Instant?,
        newImages: List<MemImgVS>,
        newFiles: Map<String, String>,
    ) -> Unit,
    onImageSelected: (imageId: Int) -> Unit,
    deleteImage: (imageId: Int) -> Unit,
    deleteFile: (fileId: Int) -> Unit,
    deleteItem: () -> Unit,
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

    var typeOverride by remember { mutableStateOf<ItemTypeVS?>(null) }
    var nameOverride by remember { mutableStateOf<String?>(null) }
    var descriptionOverride by remember { mutableStateOf<String?>(null) }
    var datesOverride by remember { mutableStateOf<List<Instant>?>(null) }
    var targetUserIdOverride by remember { mutableStateOf<Int?>(null) }
    var urlOverride by remember { mutableStateOf<String?>(null) }
    var startOverride by remember { mutableStateOf<Instant?>(null) }
    var endOverride by remember { mutableStateOf<Instant?>(null) }
    var newImages by remember { mutableStateOf<List<MemImgVS>>(emptyList()) }
    var newFiles by remember { mutableStateOf<Map<String, String>>(emptyMap()) }

    val hasChanged by derivedStateOf {
        listOf(
            typeOverride,
            nameOverride,
            descriptionOverride,
            datesOverride,
            targetUserIdOverride,
            urlOverride,
            startOverride,
            endOverride,
        ).any { it != null }
                || newImages.isNotEmpty()
                || newFiles.isNotEmpty()
    }

    LaunchedEffect(item) {
        typeOverride = null
        nameOverride = null
        descriptionOverride = null
        datesOverride = null
        targetUserIdOverride = null
        urlOverride = null
        startOverride = null
        endOverride = null
        newImages = emptyList()
        newFiles = emptyMap()
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
                newImages += MemImgVS(it, img)
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
            newFiles += (it to loadNameFromFile(it))
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
            it?.let { ts ->
                val instant = fromEpochSeconds(ts.toLong())
                datesOverride = (datesOverride ?: item.dates).toMutableList().apply {
                    if (showDatePickerIndex == -1) {
                        this += instant
                    } else {
                        this[showDatePickerIndex] = instant
                    }
                }.sorted()
            }
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
                    (if (isAdmin) TYPE_ASSOC_ADMIN else TYPE_ASSOC).forEach { (type, iconNamePair) ->
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
                        nameOverride = it
                    },
                    isError = nameOverride?.isBlank() ?: false,
                    label = { Text(stringResource(Res.string.item_name)) },
                    modifier = Modifier.fillMaxWidth(),
                )

                AnimatedVisibility((typeOverride ?: item.type) != REMINDER) {
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
                            value = urlOverride ?: item.url,
                            onValueChange = {
                                urlOverride = it
                            },
                            isError = urlOverride?.let { it.isBlank() || !it.isValidUrl() }
                                ?: false,
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

                if (item.images.size > 0 || newImages.size > 0) {
                    ImageGrid(
                        images = item.images,
                        newImages = newImages,
                        deleteNew = { delImg ->
                            newImages = newImages.filter { it != delImg }
                        },
                        delete = {
                            deleteImage(it.id)
                        },
                        select = {
                            onImageSelected(it.id)
                        }
                    )
                }

                AnimatedVisibility((typeOverride ?: item.type) == REMINDER) {
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
                                    (datesOverride ?: item.dates).lastOrNull() ?: Clock.System.now()
                            }, text = stringResource(Res.string.add_date), bold = true)
                        }

                        (datesOverride ?: item.dates).forEachIndexed { index, date ->
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
                                        datesOverride =
                                            (datesOverride ?: item.dates).toMutableList().apply {
                                                this.removeAt(index)
                                            }
                                    },
                                    text = stringResource(Res.string.delete),
                                    bold = true
                                )
                            }
                        }
                    }
                }

                AnimatedVisibility((typeOverride ?: item.type) != REMINDER) {
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
                                    modifier = Modifier.weight(1f).padding(end = 5.dp).clickable {
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

                        newFiles.onEach { newFile ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                FriendlyText(
                                    modifier = Modifier
                                        .weight(1f).padding(end = 5.dp)
                                        .clickable {
                                            showNewFileAlert = true
                                        }, text = newFile.value, bold = true
                                )
                                Image(
                                    modifier = Modifier.size(36.dp),
                                    painter = badge,
                                    contentScale = ContentScale.FillBounds,
                                    contentDescription = "Item File New Badge",
                                    colorFilter = ColorFilter.tint(AppColors.primary),
                                )
                                FriendlyText(
                                    modifier = Modifier.wrapContentWidth().clickable {
                                        newFiles = newFiles.filter { it != newFile }
                                    },
                                    text = stringResource(Res.string.delete),
                                    bold = true
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

                if (hasChanged) {
                    FriendlyButton(
                        text = stringResource(Res.string.save),
                        loading = saving,
                    ) {
                        onSave(
                            typeOverride,
                            nameOverride,
                            descriptionOverride,
                            datesOverride,
                            targetUserIdOverride,
                            urlOverride,
                            startOverride,
                            endOverride,
                            newImages,
                            newFiles,
                        )
                    }
                }

                if (error.isNotEmpty()) {
                    FriendlyErrorText(error)
                }
            }
        }
        CardFooter {
            if (item.id != null) {
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