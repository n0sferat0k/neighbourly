package com.neighbourly.app.a_device.ui.atomic.molecule.item

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.neighbourly.app.a_device.ui.atomic.atom.FriendlyText
import com.neighbourly.app.a_device.ui.atomic.page.TYPE_ASSOC
import com.neighbourly.app.a_device.ui.atomic.page.TYPE_ASSOC_ADMIN
import com.neighbourly.app.b_adapt.viewmodel.bean.ItemTypeVS
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.type
import neighbourly.composeapp.generated.resources.unknown
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ItemEditTypeSelector(type: ItemTypeVS, isAdmin: Boolean, onChange: (type: ItemTypeVS) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth()) {
        FriendlyText(text = stringResource(Res.string.type))

        FriendlyText(
            text = stringResource(
                TYPE_ASSOC_ADMIN.get(type)?.second ?: Res.string.unknown
            ),
            bold = true,
        )
    }

    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.Start,
    ) {
        (if (isAdmin) TYPE_ASSOC_ADMIN else TYPE_ASSOC).forEach { (currentType, iconNamePair) ->
            ItemTypeOption(
                icon = painterResource(iconNamePair.first),
                selected = type == currentType,
                contentDesc = currentType.name,
            ) {
                onChange(currentType)
            }
        }
    }
}