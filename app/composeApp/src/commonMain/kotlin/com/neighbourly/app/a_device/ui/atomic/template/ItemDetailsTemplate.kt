package com.neighbourly.app.a_device.ui.atomic.template

import androidx.compose.runtime.Composable
import com.neighbourly.app.a_device.ui.atomic.organism.item.OrganismEditableItemDetails
import com.neighbourly.app.a_device.ui.atomic.organism.item.OrganismReadonlyItemDetails
import com.neighbourly.app.b_adapt.viewmodel.bean.ItemTypeVS
import com.neighbourly.app.b_adapt.viewmodel.bean.MemImgVS
import com.neighbourly.app.b_adapt.viewmodel.items.ItemDetailsViewModel
import kotlinx.datetime.Instant

@Composable
fun ItemDetailsTemplate(
    state: ItemDetailsViewModel.ItemDetailsViewState,
    onImageSelected: (itemId: Int, imageId: Int) -> Unit,
    onUrlSelected: (url: String) -> Unit,
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
    deleteImage: (imageId: Int) -> Unit,
    deleteFile: (fileId: Int) -> Unit,
    deleteItem: () -> Unit,
) {
    state.item?.let { item ->
        if (state.editable) {
            OrganismEditableItemDetails(
                item = item,
                users = state.users,
                isAdmin = state.admin,
                error = state.error,
                saving = state.saving,
                onSave = onSave,
                onImageSelected = { imageId ->
                    state.item.id?.let {
                        onImageSelected(
                            it,
                            imageId
                        )
                    }
                },
                deleteImage = deleteImage,
                deleteFile = deleteFile,
                deleteItem = deleteItem,
            )

        } else {
            OrganismReadonlyItemDetails(
                item = item,
                users = state.users,
                onImageSelected = { imageId ->
                    state.item.id?.let {
                        onImageSelected(
                            it,
                            imageId
                        )
                    }
                },
                onUrlSelected = onUrlSelected
            )
        }

    }
}