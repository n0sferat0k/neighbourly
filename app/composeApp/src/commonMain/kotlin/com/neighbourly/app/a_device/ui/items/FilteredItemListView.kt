package com.neighbourly.app.a_device.ui.items

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.neighbourly.app.KoinProvider
import com.neighbourly.app.a_device.ui.AlertDialog
import com.neighbourly.app.a_device.ui.AppColors
import com.neighbourly.app.a_device.ui.BoxFooter
import com.neighbourly.app.a_device.ui.BoxHeader
import com.neighbourly.app.a_device.ui.BoxStaticContent
import com.neighbourly.app.a_device.ui.CurlyText
import com.neighbourly.app.a_device.ui.SwipeToDeleteBox
import com.neighbourly.app.b_adapt.viewmodel.items.FilteredItemListViewModel
import com.neighbourly.app.b_adapt.viewmodel.items.FilteredItemListViewModel.ItemTypeVS.BARTER
import com.neighbourly.app.b_adapt.viewmodel.items.FilteredItemListViewModel.ItemTypeVS.DONATION
import com.neighbourly.app.b_adapt.viewmodel.items.FilteredItemListViewModel.ItemTypeVS.EVENT
import com.neighbourly.app.b_adapt.viewmodel.items.FilteredItemListViewModel.ItemTypeVS.INFO
import com.neighbourly.app.b_adapt.viewmodel.items.FilteredItemListViewModel.ItemTypeVS.NEED
import com.neighbourly.app.b_adapt.viewmodel.items.FilteredItemListViewModel.ItemTypeVS.REQUEST
import com.neighbourly.app.b_adapt.viewmodel.items.FilteredItemListViewModel.ItemTypeVS.SALE
import com.neighbourly.app.b_adapt.viewmodel.items.FilteredItemListViewModel.ItemTypeVS.SKILLSHARE
import com.neighbourly.app.b_adapt.viewmodel.items.FilteredItemListViewModel.ItemVS
import com.neighbourly.app.b_adapt.viewmodel.navigation.NavigationViewModel
import com.neighbourly.app.d_entity.data.ItemType
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.barter
import neighbourly.composeapp.generated.resources.confirm_deleteing_item
import neighbourly.composeapp.generated.resources.deleteing_item
import neighbourly.composeapp.generated.resources.donate
import neighbourly.composeapp.generated.resources.event
import neighbourly.composeapp.generated.resources.file
import neighbourly.composeapp.generated.resources.filter
import neighbourly.composeapp.generated.resources.hourglass
import neighbourly.composeapp.generated.resources.image
import neighbourly.composeapp.generated.resources.info
import neighbourly.composeapp.generated.resources.need
import neighbourly.composeapp.generated.resources.request
import neighbourly.composeapp.generated.resources.sale
import neighbourly.composeapp.generated.resources.skillshare
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun FilteredItemListView(
    type: ItemType? = null,
    householdId: Int? = null,
    showExpired: Boolean = false,
    viewModel: FilteredItemListViewModel = viewModel { KoinProvider.KOIN.get<FilteredItemListViewModel>() },
) {
    val state by viewModel.state.collectAsState()
    var showRemoveAlertForId by remember { mutableStateOf(-1) }

    LaunchedEffect(type, householdId, showExpired) {
        viewModel.setFilters(type, householdId, showExpired)
    }

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        BoxHeader(Modifier.align(Alignment.Start), busy = state.loading) {
            viewModel.refresh(true)
        }

        BoxStaticContent(modifier = Modifier.weight(1f)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(items = state.items) { item ->

                    if (showRemoveAlertForId == item.id) {
                        AlertDialog(
                            title = stringResource(Res.string.deleteing_item),
                            text = stringResource(Res.string.confirm_deleteing_item) + " " + item.name,
                            ok = {
                                showRemoveAlertForId = -1
                                viewModel.onDeleteItem(item.id)
                            },
                            cancel = {
                                showRemoveAlertForId = -1
                            }
                        )
                    }

                    if (item.deletable) {
                        SwipeToDeleteBox(onDelete = {
                            showRemoveAlertForId = item.id
                        }) {
                            ItemCard(item)
                        }
                    } else {
                        ItemCard(item)
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
        BoxFooter(modifier = Modifier.align(Alignment.End)) {
            CurlyText(text = stringResource(Res.string.filter), bold = true)
        }
    }
}

@Composable
fun ItemCard(
    item: ItemVS,
    navigationViewModel: NavigationViewModel = viewModel { KoinProvider.KOIN.get<NavigationViewModel>() }
) {
    val imgTag = painterResource(Res.drawable.image)
    val fileTag = painterResource(Res.drawable.file)
    val expTag = painterResource(Res.drawable.hourglass)

    val defaultItemImg = when (item.type) {
        INFO -> painterResource(Res.drawable.info)
        DONATION -> painterResource(Res.drawable.donate)
        BARTER -> painterResource(Res.drawable.barter)
        SALE -> painterResource(Res.drawable.sale)
        EVENT -> painterResource(Res.drawable.event)
        NEED -> painterResource(Res.drawable.need)
        REQUEST -> painterResource(Res.drawable.request)
        SKILLSHARE -> painterResource(Res.drawable.skillshare)
    }

    Card(
        modifier = Modifier.fillMaxWidth().clickable {
                navigationViewModel.goToItemDetails(item.id)
        },
        shape = RoundedCornerShape(8.dp),
        elevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                if (item.imageUrl.isNullOrBlank()) {
                    Image(
                        modifier = Modifier.size(56.dp),
                        painter = defaultItemImg,
                        contentDescription = "Item Image",
                        colorFilter = ColorFilter.tint(AppColors.primary),
                    )
                } else {
                    Box(modifier = Modifier.size(56.dp)) {
                        KamelImage(
                            modifier = Modifier.size(48.dp)
                                .shadow(elevation = 3.dp, ambientColor = AppColors.primary),
                            resource = asyncPainterResource(data = item.imageUrl),
                            contentDescription = "Item Image",
                            contentScale = ContentScale.Crop,
                            onLoading = { progress ->
                                CircularProgressIndicator(
                                    progress = progress,
                                    color = AppColors.primary,
                                )
                            },
                        )
                        Image(
                            modifier = Modifier.size(18.dp).background(Color.White, CircleShape)
                                .align(Alignment.BottomEnd).clip(CircleShape),
                            contentScale = ContentScale.Fit,
                            painter = defaultItemImg,
                            contentDescription = "Household Image",
                            colorFilter = ColorFilter.tint(AppColors.primary),
                        )
                    }
                }


                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Text(text = item.name, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Text(text = item.description, fontSize = 14.sp, lineHeight = 14.sp)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                if (item.imgCount > 0) {
                    Badge(imgTag, item.imgCount.toString())
                }

                if (item.fileCount > 0) {
                    Badge(fileTag, item.fileCount.toString())
                }

                if (item.expLabel != null) {
                    Badge(expTag, item.expLabel, AppColors.complementary)
                }
            }
        }
    }
}

@Composable
fun Badge(imgPainer: Painter, text: String, color: Color = AppColors.primary) {
    Row(
        modifier = Modifier
            .background(Color.White, shape = RoundedCornerShape(4.dp))
            .border(1.dp, color, RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Image(
            modifier = Modifier.size(18.dp).align(Alignment.CenterVertically),
            contentScale = ContentScale.Fit,
            painter = imgPainer,
            contentDescription = "Badge Image",
            colorFilter = ColorFilter.tint(color),
        )
        Text(
            modifier = Modifier.align(Alignment.CenterVertically),
            text = ": $text",
            color = color,
            fontSize = 12.sp
        )
    }
}
