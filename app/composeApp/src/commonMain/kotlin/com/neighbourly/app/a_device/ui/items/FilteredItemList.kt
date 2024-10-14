package com.neighbourly.app.a_device.ui.items

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.neighbourly.app.KoinProvider
import com.neighbourly.app.a_device.ui.AppColors
import com.neighbourly.app.a_device.ui.BoxHeader
import com.neighbourly.app.a_device.ui.BoxStaticContent
import com.neighbourly.app.a_device.ui.ErrorText
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
import neighbourly.composeapp.generated.resources.donate
import neighbourly.composeapp.generated.resources.event
import neighbourly.composeapp.generated.resources.expires_in
import neighbourly.composeapp.generated.resources.file
import neighbourly.composeapp.generated.resources.image
import neighbourly.composeapp.generated.resources.info
import neighbourly.composeapp.generated.resources.need
import neighbourly.composeapp.generated.resources.request
import neighbourly.composeapp.generated.resources.sale
import neighbourly.composeapp.generated.resources.skillshare
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun FilteredItemList(
    type: ItemType? = null,
    householdId: Int? = null,
    viewModel: FilteredItemListViewModel = viewModel { KoinProvider.KOIN.get<FilteredItemListViewModel>() },
    navigationViewModel: NavigationViewModel = viewModel { KoinProvider.KOIN.get<NavigationViewModel>() }
) {
    val state by viewModel.state.collectAsState()
    val navigation by navigationViewModel.state.collectAsState()

    LaunchedEffect(type) {
        viewModel.setFilters(type, householdId)
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
                items(state.items) { item ->
                    ItemCard(item)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}


@Composable
fun ItemCard(item: ItemVS) {
    val imgTag = painterResource(Res.drawable.image)
    val fileTag = painterResource(Res.drawable.file)

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
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        elevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (item.imageUrl.isNullOrBlank()) {
                    Image(
                        modifier = Modifier.size(56.dp),
                        painter = defaultItemImg,
                        contentDescription = "Household Image",
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
                    Text(text = item.description, fontSize = 14.sp)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                if (item.imgCount > 0) {
                    Row(
                        modifier = Modifier
                            .background(Color.White, shape = RoundedCornerShape(4.dp))
                            .border(1.dp, AppColors.primary, RoundedCornerShape(4.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Image(
                            modifier = Modifier.size(18.dp).align(Alignment.CenterVertically),
                            contentScale = ContentScale.Fit,
                            painter = imgTag,
                            contentDescription = "Badge Image",
                            colorFilter = ColorFilter.tint(AppColors.primary),
                        )
                        Text(
                            modifier = Modifier.align(Alignment.CenterVertically),
                            text = ": ${item.imgCount}",
                            color = AppColors.primary,
                            fontSize = 12.sp
                        )
                    }
                }

                if (item.fileCount > 0) {
                    Row(
                        modifier = Modifier
                            .background(Color.White, shape = RoundedCornerShape(4.dp))
                            .border(1.dp, AppColors.primary, RoundedCornerShape(4.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Image(
                            modifier = Modifier.size(18.dp).align(Alignment.CenterVertically),
                            contentScale = ContentScale.Fit,
                            painter = fileTag,
                            contentDescription = "Badge Image",
                            colorFilter = ColorFilter.tint(AppColors.primary),
                        )
                        Text(
                            modifier = Modifier.align(Alignment.CenterVertically),
                            text = ": ${item.fileCount}",
                            color = AppColors.primary,
                            fontSize = 12.sp
                        )
                    }
                }

                if (item.endsSec != null && item.endsSec > 0) {
                    val days = item.endsSec / (24 * 3600)
                    val hours = (item.endsSec % (24 * 3600)) / 3600
                    val minutes = (item.endsSec % 3600) / 60
                    ErrorText(errMsg = stringResource(Res.string.expires_in) + "$days D, $hours : $minutes")
                }
            }
        }
    }
}
