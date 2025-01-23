package com.neighbourly.app.a_device.ui.atomic.organism.menu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.neighbourly.app.a_device.ui.atomic.molecule.menu.LateralMenuItem
import com.neighbourly.app.a_device.ui.atomic.molecule.menu.MenuItemBoxSide.LEFT
import com.neighbourly.app.a_device.ui.atomic.molecule.menu.MenuItemBoxSide.RIGHT
import com.neighbourly.app.b_adapt.viewmodel.bean.MenuTabVS
import com.neighbourly.app.b_adapt.viewmodel.bean.MenuTabVS.BOX
import com.neighbourly.app.b_adapt.viewmodel.bean.MenuTabVS.ITEMS
import com.neighbourly.app.b_adapt.viewmodel.bean.MenuTabVS.MYSTUFF
import com.neighbourly.app.b_adapt.viewmodel.bean.MenuTabVS.PROFILE
import com.neighbourly.app.b_adapt.viewmodel.bean.MenuTabVS.PUBLISH
import com.neighbourly.app.b_adapt.viewmodel.bean.MenuTabVS.REMINDERS
import com.neighbourly.app.d_entity.data.ItemType
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.barter
import neighbourly.composeapp.generated.resources.barterings
import neighbourly.composeapp.generated.resources.box
import neighbourly.composeapp.generated.resources.donate
import neighbourly.composeapp.generated.resources.donations
import neighbourly.composeapp.generated.resources.event
import neighbourly.composeapp.generated.resources.events
import neighbourly.composeapp.generated.resources.layers
import neighbourly.composeapp.generated.resources.mystuff
import neighbourly.composeapp.generated.resources.need
import neighbourly.composeapp.generated.resources.needs
import neighbourly.composeapp.generated.resources.onboard
import neighbourly.composeapp.generated.resources.profile
import neighbourly.composeapp.generated.resources.publish
import neighbourly.composeapp.generated.resources.reminder
import neighbourly.composeapp.generated.resources.reminders
import neighbourly.composeapp.generated.resources.request
import neighbourly.composeapp.generated.resources.requests
import neighbourly.composeapp.generated.resources.sale
import neighbourly.composeapp.generated.resources.sales
import neighbourly.composeapp.generated.resources.skillshare
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource


@Composable
fun OrganismOverlayMenuLimited(onSelect: (tab: MenuTabVS) -> Unit) {
    Row(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxHeight().weight(.5f),
            verticalArrangement = Arrangement.spacedBy(15.dp, Alignment.Top),
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            LateralMenuItem(
                side = LEFT,
                modifier = Modifier.align(Alignment.Start),
                text = stringResource(Res.string.profile),
                image = painterResource(Res.drawable.onboard),
                delayMs = 100
            ) {
                onSelect(PROFILE)
            }

            LateralMenuItem(
                side = LEFT,
                modifier = Modifier.align(Alignment.Start),
                text = stringResource(Res.string.box),
                image = painterResource(Res.drawable.box),
                delayMs = 200
            ) {
                onSelect(BOX)
            }
        }
    }
}