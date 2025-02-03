package com.neighbourly.app.a_device.ui.atomic.template

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.neighbourly.app.a_device.ui.atomic.molecule.card.DeleteCardFooter
import com.neighbourly.app.a_device.ui.atomic.organism.item.OrganismEditableItemDetails
import com.neighbourly.app.a_device.ui.atomic.organism.item.OrganismItemMessages
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
    onPostItemMessage: (message: String) -> Unit,
    onDeleteItemMessage: (messageId: Int) -> Unit,
    onSelectHousehold: (id: Int) -> Unit,
) {
    state.item?.let { item ->
        OrganismContentBubble(
            scrollable = false,
            busy = state.saving,
            content = {
                LazyColumn(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
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
                    }
                    OrganismItemMessages(
                        messages = item.messages,
                        onPostMessage = onPostItemMessage,
                        onDeleteMessage = onDeleteItemMessage,
                        onSelectHousehold = onSelectHousehold
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