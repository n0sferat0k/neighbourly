package com.neighbourly.app.a_device.ui.atomic.template

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.neighbourly.app.a_device.ui.atomic.atom.FriendlyText
import com.neighbourly.app.a_device.ui.atomic.molecule.SwipeToDeleteContainer
import com.neighbourly.app.a_device.ui.atomic.molecule.card.CardFooter
import com.neighbourly.app.a_device.ui.atomic.molecule.card.CardHeader
import com.neighbourly.app.a_device.ui.atomic.molecule.card.CardStaticContent
import com.neighbourly.app.a_device.ui.atomic.molecule.card.RoundedCornerCard
import com.neighbourly.app.a_device.ui.atomic.organism.item.OrganismItemCard
import com.neighbourly.app.a_device.ui.atomic.organism.util.OrganismAlertDialog
import com.neighbourly.app.b_adapt.viewmodel.items.FilteredItemListViewModel.FilteredItemListViewState
import com.neighbourly.app.d_entity.data.ItemType
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.confirm_deleteing_item
import neighbourly.composeapp.generated.resources.deleteing_item
import neighbourly.composeapp.generated.resources.filter
import org.jetbrains.compose.resources.stringResource

@Composable
fun FilteredItemListTemplate(
    state: FilteredItemListViewState,
    onDeleteItem: (id: Int) -> Unit,
    refresh: () -> Unit,
) {
    var showRemoveAlertForId by remember { mutableStateOf(-1) }

    if (showRemoveAlertForId != -1) {
        state.items.firstOrNull { it.id == showRemoveAlertForId }?.let { item ->
            OrganismAlertDialog(
                title = stringResource(Res.string.deleteing_item),
                text = stringResource(Res.string.confirm_deleteing_item) + " " + item.name,
                ok = {
                    showRemoveAlertForId = -1
                    onDeleteItem(item.id)
                },
                cancel = {
                    showRemoveAlertForId = -1
                }
            )
        }
    }
    RoundedCornerCard {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            CardHeader(Modifier.align(Alignment.Start), busy = state.loading) {
                refresh()
            }

            CardStaticContent(modifier = Modifier.weight(1f)) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(items = state.items, key = { it.id }) { item ->
                        if (item.deletable) {
                            SwipeToDeleteContainer(onDelete = {
                                showRemoveAlertForId = item.id
                            }) {
                                OrganismItemCard(item)
                            }
                        } else {
                            OrganismItemCard(item)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
            CardFooter {
                FriendlyText(text = stringResource(Res.string.filter), bold = true)
            }
        }
    }
}

