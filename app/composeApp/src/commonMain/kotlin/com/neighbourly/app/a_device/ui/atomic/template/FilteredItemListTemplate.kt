package com.neighbourly.app.a_device.ui.atomic.template

import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.neighbourly.app.a_device.ui.atomic.atom.FriendlyText
import com.neighbourly.app.a_device.ui.atomic.organism.item.OrganismItemList
import com.neighbourly.app.a_device.ui.atomic.organism.util.OrganismContentBubble
import com.neighbourly.app.b_adapt.viewmodel.items.FilteredItemListViewModel.FilteredItemListViewState
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.add
import org.jetbrains.compose.resources.stringResource

@Composable
fun FilteredItemListTemplate(
    state: FilteredItemListViewState,
    onDeleteItem: (id: Int) -> Unit,
    onSelectItem: (id: Int) -> Unit,
    onSelectHousehold: (id: Int) -> Unit,
    refresh: () -> Unit,
    add: () -> Unit,
) {
    OrganismContentBubble(
        scrollable = false,
        busy = state.loading,
        refresh = refresh,
        content = {
            OrganismItemList(
                state.items,
                onDeleteItem = onDeleteItem,
                onSelectItem = onSelectItem,
                onSelectHousehold = onSelectHousehold
            )
        },
        footerContent = {
            FriendlyText(modifier = Modifier.clickable {
                add()
            }, text = stringResource(Res.string.add), bold = true)
        }
    )
}

