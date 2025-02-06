package com.neighbourly.app.a_device.ui.atomic.molecule.item

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.neighbourly.app.a_device.ui.AppColors
import com.neighbourly.app.a_device.ui.atomic.atom.FriendlyText
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.for_all_neighbourhood
import org.jetbrains.compose.resources.stringResource

@Composable
fun ItemEditForAllNeighbourhood(
    hidden: Boolean = false,
    selected: Boolean,
    onSelect: (accent: Boolean) -> Unit
) {
    AnimatedVisibility(!hidden) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            FriendlyText(text = stringResource(Res.string.for_all_neighbourhood))
            Checkbox(
                checked = selected,
                onCheckedChange = { onSelect(it) },
                colors = CheckboxDefaults.colors(
                    checkedColor = AppColors.primary,
                    uncheckedColor = AppColors.primary,
                    checkmarkColor = Color.White
                )
            )
        }
    }
}