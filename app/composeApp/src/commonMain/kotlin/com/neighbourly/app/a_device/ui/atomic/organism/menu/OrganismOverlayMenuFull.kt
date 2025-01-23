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
fun OrganismOverlayMenuFull(onSelect: (tab: MenuTabVS) -> Unit) {
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
                text = stringResource(Res.string.mystuff),
                image = painterResource(Res.drawable.layers),
                delayMs = 200
            ) {
                onSelect(MYSTUFF)
            }

            LateralMenuItem(
                side = LEFT,
                modifier = Modifier.align(Alignment.Start),
                text = stringResource(Res.string.publish),
                image = painterResource(Res.drawable.publish),
                delayMs = 300
            ) {
                onSelect(PUBLISH)
            }

            LateralMenuItem(
                side = LEFT,
                modifier = Modifier.align(Alignment.Start),
                text = stringResource(Res.string.reminders),
                image = painterResource(Res.drawable.reminder),
                delayMs = 400
            ) {
                onSelect(REMINDERS)
            }

            LateralMenuItem(
                side = LEFT,
                modifier = Modifier.align(Alignment.Start),
                text = stringResource(Res.string.box),
                image = painterResource(Res.drawable.box),
                delayMs = 500
            ) {
                onSelect(BOX)
            }
        }

        Column(
            modifier = Modifier.fillMaxHeight().weight(.5f),
            verticalArrangement = Arrangement.spacedBy(15.dp, Alignment.Top),
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            LateralMenuItem(
                side = RIGHT,
                modifier = Modifier.align(Alignment.End),
                text = stringResource(Res.string.donations),
                image = painterResource(Res.drawable.donate),
                delayMs = 150
            ) {
                onSelect(ITEMS(ItemType.DONATION))
            }
            LateralMenuItem(
                side = RIGHT,
                modifier = Modifier.align(Alignment.End),
                text = stringResource(Res.string.barterings),
                image = painterResource(Res.drawable.barter),
                delayMs = 250
            ) {
                onSelect(ITEMS(ItemType.BARTER))
            }
            LateralMenuItem(
                side = RIGHT,
                modifier = Modifier.align(Alignment.End),
                text = stringResource(Res.string.sales),
                image = painterResource(Res.drawable.sale),
                delayMs = 350
            ) {
                onSelect(ITEMS(ItemType.SALE))
            }
            LateralMenuItem(
                side = RIGHT,
                modifier = Modifier.align(Alignment.End),
                text = stringResource(Res.string.events),
                image = painterResource(Res.drawable.event),
                delayMs = 450
            ) {
                onSelect(ITEMS(ItemType.EVENT))
            }
            LateralMenuItem(
                side = RIGHT,
                modifier = Modifier.align(Alignment.End),
                text = stringResource(Res.string.needs),
                image = painterResource(Res.drawable.need),
                delayMs = 550
            ) {
                onSelect(ITEMS(ItemType.NEED))
            }
            LateralMenuItem(
                side = RIGHT,
                modifier = Modifier.align(Alignment.End),
                text = stringResource(Res.string.requests),
                image = painterResource(Res.drawable.request),
                delayMs = 650
            ) {
                onSelect(ITEMS(ItemType.REQUEST))
            }
            LateralMenuItem(
                side = RIGHT,
                modifier = Modifier.align(Alignment.End),
                text = stringResource(Res.string.skillshare),
                image = painterResource(Res.drawable.skillshare),
                delayMs = 750
            ) {
                onSelect(ITEMS(ItemType.SKILLSHARE))
            }
        }
    }
}