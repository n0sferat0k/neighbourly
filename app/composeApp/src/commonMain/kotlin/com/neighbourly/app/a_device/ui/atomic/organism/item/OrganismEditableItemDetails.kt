package com.neighbourly.app.a_device.ui.atomic.organism.item

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.neighbourly.app.a_device.ui.atomic.atom.FriendlyButton
import com.neighbourly.app.a_device.ui.atomic.atom.FriendlyErrorText
import com.neighbourly.app.a_device.ui.atomic.molecule.item.ItemEditDateSeries
import com.neighbourly.app.a_device.ui.atomic.molecule.item.ItemEditDateStartEnd
import com.neighbourly.app.a_device.ui.atomic.molecule.item.ItemEditDescription
import com.neighbourly.app.a_device.ui.atomic.molecule.item.ItemEditFiles
import com.neighbourly.app.a_device.ui.atomic.molecule.item.ItemEditImages
import com.neighbourly.app.a_device.ui.atomic.molecule.item.ItemEditName
import com.neighbourly.app.a_device.ui.atomic.molecule.item.ItemEditTargetUser
import com.neighbourly.app.a_device.ui.atomic.molecule.item.ItemEditTypeSelector
import com.neighbourly.app.a_device.ui.atomic.molecule.item.ItemEditUrl
import com.neighbourly.app.b_adapt.viewmodel.bean.ItemTypeVS
import com.neighbourly.app.b_adapt.viewmodel.bean.ItemTypeVS.NEED
import com.neighbourly.app.b_adapt.viewmodel.bean.ItemTypeVS.REMINDER
import com.neighbourly.app.b_adapt.viewmodel.bean.ItemTypeVS.REQUEST
import com.neighbourly.app.b_adapt.viewmodel.bean.ItemVS
import com.neighbourly.app.b_adapt.viewmodel.bean.MemImgVS
import com.neighbourly.app.d_entity.util.isValidUrl
import kotlinx.datetime.Instant
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.save
import org.jetbrains.compose.resources.stringResource

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
    onUrlSelected: (url: String) -> Unit,
    onDeleteImage: (imageId: Int) -> Unit,
    onDeleteFile: (fileId: Int) -> Unit,
) {
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

    Column(
        modifier = Modifier.padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        ItemEditTypeSelector(
            type = typeOverride ?: item.type,
            isAdmin = isAdmin
        ) { type ->
            typeOverride = type
            if (!listOf(NEED, REQUEST).contains(type)) {
                targetUserIdOverride = -1
            }
        }

        ItemEditTargetUser(
            hidden = !listOf(NEED, REQUEST).contains(typeOverride ?: item.type),
            selectedUserId = (item.targetUserId ?: targetUserIdOverride),
            users
        ) {
            targetUserIdOverride = it
        }

        ItemEditName(
            name = nameOverride ?: item.name,
            isError = nameOverride?.isBlank() ?: false
        ) {
            nameOverride = it
        }

        ItemEditDescription(
            hidden = (typeOverride ?: item.type) == REMINDER,
            description = descriptionOverride ?: item.description
        ) {
            descriptionOverride = it
        }

        ItemEditUrl(
            hidden = (typeOverride ?: item.type) == REMINDER,
            url = urlOverride ?: item.url,
            isError = urlOverride?.let { !it.isBlank() && !it.isValidUrl() } ?: false
        ) {
            urlOverride = it
        }

        ItemEditImages(
            images = item.images,
            newImages = newImages,
            onDeleteImage = onDeleteImage,
            onImageSelected = onImageSelected,
        ) {
            newImages = it
        }

        ItemEditFiles(
            files = item.files,
            newFiles = newFiles,
            onUrlSelected = onUrlSelected,
            onDeleteFile = onDeleteFile
        ) {
            newFiles = it
        }

        ItemEditDateSeries(
            hidden = (typeOverride ?: item.type) != REMINDER,
            dates = (datesOverride ?: item.dates)
        ) {
            datesOverride = it
        }

        ItemEditDateStartEnd(
            hidden = (typeOverride ?: item.type) == REMINDER,
            start = startOverride ?: item.start,
            end = endOverride ?: item.end,
            onStartChange = {
                startOverride = it
            },
            onEndChange = {
                endOverride = it
            })

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