package com.neighbourly.app.a_device.ui.atomic.template

import androidx.compose.runtime.Composable
import com.neighbourly.app.a_device.ui.atomic.molecule.card.DeleteCardFooter
import com.neighbourly.app.a_device.ui.atomic.organism.item.OrganismEditableItemDetails
import com.neighbourly.app.a_device.ui.atomic.organism.item.OrganismReadonlyItemDetails
import com.neighbourly.app.a_device.ui.atomic.organism.util.OrganismContentBubble
import com.neighbourly.app.b_adapt.viewmodel.bean.ItemTypeVS
import com.neighbourly.app.b_adapt.viewmodel.bean.MemImgVS
import com.neighbourly.app.b_adapt.viewmodel.items.ItemDetailsViewModel
import kotlinx.datetime.Instant

@Composable
fun ItemDetailsTemplate(
    state: ItemDetailsViewModel.ItemDetailsViewState,
    onImageSelected: (imageId: Int) -> Unit,
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
    onDeleteImage: (imageId: Int) -> Unit,
    onDeleteFile: (fileId: Int) -> Unit,
    onDeleteItem: () -> Unit,
) {
    state.item?.let { item ->
        OrganismContentBubble(
            scrollable = true,
            busy = state.saving,
            content = {
                if (state.editable) {
                    OrganismEditableItemDetails(
                        item = item,
                        users = state.users,
                        isAdmin = state.admin,
                        error = state.error,
                        saving = state.saving,
                        onSave = onSave,
                        onImageSelected = onImageSelected,
                        onUrlSelected = onUrlSelected,
                        onDeleteImage = onDeleteImage,
                        onDeleteFile = onDeleteFile,
                    )
                } else {
                    OrganismReadonlyItemDetails(
                        item = item,
                        users = state.users,
                        onImageSelected = onImageSelected,
                        onUrlSelected = onUrlSelected,
                    )
                }
            },
            footerContent = {
                if (item.id != null && state.editable) {
                    DeleteCardFooter {
                        onDeleteItem()
                    }
                }
            }
        )
    }
}