package com.neighbourly.app.a_device.ui.atomic.template

import androidx.compose.runtime.Composable
import com.neighbourly.app.a_device.ui.atomic.organism.item.OrganismEditableItemDetails
import com.neighbourly.app.a_device.ui.atomic.organism.item.OrganismReadonlyItemDetails
import com.neighbourly.app.b_adapt.viewmodel.items.ItemDetailsViewModel

@Composable
fun ItemDetailsTemplate(
    state: ItemDetailsViewModel.ItemDetailsViewState,
    onImageSelected: (itemId:Int, imageId: Int) -> Unit,
    onUrlSelected: (url: String) -> Unit,
) {
    if (state.editable) {
        OrganismEditableItemDetails(state)
    } else {
        OrganismReadonlyItemDetails(
            item = state.item,
            users = state.users,
            onImageSelected = onImageSelected,
            onUrlSelected = onUrlSelected
        )
    }
}