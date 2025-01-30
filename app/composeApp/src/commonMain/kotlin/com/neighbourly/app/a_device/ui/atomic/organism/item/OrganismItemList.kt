package com.neighbourly.app.a_device.ui.atomic.organism.item

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.neighbourly.app.a_device.ui.atomic.molecule.misc.SwipeToDeleteContainer
import com.neighbourly.app.a_device.ui.atomic.organism.util.OrganismAlertDialog
import com.neighbourly.app.b_adapt.viewmodel.bean.ItemVS
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.confirm_deleteing_item
import neighbourly.composeapp.generated.resources.deleteing_item
import org.jetbrains.compose.resources.stringResource


@Composable
fun OrganismItemList(
    items: List<ItemVS>,
    onSelectItem: (id: Int) -> Unit,
    onDeleteItem: (id: Int) -> Unit,
    onSelectHousehold: (id: Int) -> Unit,
) {
    var showRemoveAlertForId by remember { mutableStateOf(-1) }

    if (showRemoveAlertForId != -1) {
        items.firstOrNull { it.id == showRemoveAlertForId }?.let { item ->
            OrganismAlertDialog(
                title = stringResource(Res.string.deleteing_item),
                text = stringResource(Res.string.confirm_deleteing_item) + " " + item.name,
                ok = {
                    showRemoveAlertForId = -1
                    item.id?.let { onDeleteItem(it) }
                },
                cancel = {
                    showRemoveAlertForId = -1
                }
            )
        }
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(items = items, key = { it.id ?: -1 }) { item ->
            if (item.augmentation?.deletable == true) {
                SwipeToDeleteContainer(onDelete = {
                    showRemoveAlertForId = item.id ?: -1
                }) {
                    OrganismItemCard(item = item, onClick = {
                        item.id?.let { onSelectItem(it) }
                    }, onHouseholdClick = {
                        item.augmentation.household?.id?.let { onSelectHousehold(it) }
                    })
                }
            } else {
                OrganismItemCard(item = item, onClick = {
                    item.id?.let { onSelectItem(it) }
                }, onHouseholdClick = {
                    item.augmentation?.household?.id?.let { onSelectHousehold(it) }
                })
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}