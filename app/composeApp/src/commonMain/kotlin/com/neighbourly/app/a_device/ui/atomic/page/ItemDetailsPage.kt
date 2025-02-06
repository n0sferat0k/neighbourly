@file:OptIn(ExperimentalLayoutApi::class)

package com.neighbourly.app.a_device.ui.atomic.page

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalUriHandler
import androidx.lifecycle.viewmodel.compose.viewModel
import com.neighbourly.app.KoinProvider
import com.neighbourly.app.a_device.ui.atomic.template.ItemDetailsTemplate
import com.neighbourly.app.b_adapt.viewmodel.bean.ItemTypeVS
import com.neighbourly.app.b_adapt.viewmodel.bean.ItemTypeVS.BARTER
import com.neighbourly.app.b_adapt.viewmodel.bean.ItemTypeVS.DONATION
import com.neighbourly.app.b_adapt.viewmodel.bean.ItemTypeVS.EVENT
import com.neighbourly.app.b_adapt.viewmodel.bean.ItemTypeVS.NEED
import com.neighbourly.app.b_adapt.viewmodel.bean.ItemTypeVS.REMINDER
import com.neighbourly.app.b_adapt.viewmodel.bean.ItemTypeVS.REQUEST
import com.neighbourly.app.b_adapt.viewmodel.bean.ItemTypeVS.SALE
import com.neighbourly.app.b_adapt.viewmodel.bean.ItemTypeVS.SKILLSHARE
import com.neighbourly.app.b_adapt.viewmodel.items.ItemDetailsViewModel
import com.neighbourly.app.b_adapt.viewmodel.navigation.NavigationViewModel
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.barter
import neighbourly.composeapp.generated.resources.bartering
import neighbourly.composeapp.generated.resources.donate
import neighbourly.composeapp.generated.resources.donation
import neighbourly.composeapp.generated.resources.event
import neighbourly.composeapp.generated.resources.need
import neighbourly.composeapp.generated.resources.reminder
import neighbourly.composeapp.generated.resources.reminders
import neighbourly.composeapp.generated.resources.request
import neighbourly.composeapp.generated.resources.sale
import neighbourly.composeapp.generated.resources.skillshare

val TYPE_ASSOC = mapOf(
    DONATION to Pair(Res.drawable.donate, Res.string.donation),
    BARTER to Pair(Res.drawable.barter, Res.string.bartering),
    SALE to Pair(Res.drawable.sale, Res.string.sale),
    EVENT to Pair(Res.drawable.event, Res.string.event),
    NEED to Pair(Res.drawable.need, Res.string.need),
    REQUEST to Pair(Res.drawable.request, Res.string.request),
    SKILLSHARE to Pair(Res.drawable.skillshare, Res.string.skillshare),
)
val TYPE_ASSOC_ADMIN = TYPE_ASSOC + mapOf(
    REMINDER to Pair(Res.drawable.reminder, Res.string.reminder),
)

val LOCALLY_ALLOWED_SITES =
    listOf("youtube.com", "youtu.be", "facebook", "pinterest", "goo.gl/photos")

@Composable
fun ItemDetailsPage(
    itemId: Int? = null,
    itemType: ItemTypeVS? = null,
    viewModel: ItemDetailsViewModel = viewModel { KoinProvider.KOIN.get<ItemDetailsViewModel>() },
    navigationViewModel: NavigationViewModel = viewModel { KoinProvider.KOIN.get<NavigationViewModel>() }
) {
    val uriHandler = LocalUriHandler.current

    val state by viewModel.state.collectAsState()

    LaunchedEffect(itemId, itemType) {
        viewModel.setItem(itemId, itemType)
    }

    LaunchedEffect(state.item) {
        if (state.item == null) {
            viewModel.reset()
            navigationViewModel.goBack()
        }
    }

    ItemDetailsTemplate(
        state = state,
        onImageSelected = { imageId ->
            state.item?.id?.let {
                navigationViewModel.goToItemGallery(it, imageId)
            }
        },
        onUrlSelected = { url ->
            if (LOCALLY_ALLOWED_SITES.any { url.contains(it) }) {
                navigationViewModel.goToWebPage(url)
            } else {
                uriHandler.openUri(url)
            }
        },
        onDeleteItem = viewModel::deleteItem,
        onDeleteFile = viewModel::deleteFile,
        onDeleteImage = viewModel::deleteImage,
        onSave = viewModel::save,
        onPostItemMessage = viewModel::onPostItemMessage,
        onDeleteItemMessage = viewModel::onDeleteItemMeddage,
        onSelectHousehold = navigationViewModel::goToHouseholdDetails,
        onWatchItem = viewModel::onWatchItem
    )
}

