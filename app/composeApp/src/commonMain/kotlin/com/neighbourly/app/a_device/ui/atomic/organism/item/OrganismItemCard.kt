package com.neighbourly.app.a_device.ui.atomic.organism.item

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neighbourly.app.a_device.ui.AppColors
import com.neighbourly.app.a_device.ui.atomic.atom.FriendlyText
import com.neighbourly.app.a_device.ui.atomic.molecule.item.ItemBadge
import com.neighbourly.app.a_device.ui.atomic.molecule.item.ItemHouseholdBadge
import com.neighbourly.app.b_adapt.viewmodel.bean.ItemTypeVS
import com.neighbourly.app.b_adapt.viewmodel.bean.ItemVS
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.barter
import neighbourly.composeapp.generated.resources.donate
import neighbourly.composeapp.generated.resources.event
import neighbourly.composeapp.generated.resources.file
import neighbourly.composeapp.generated.resources.hourglass
import neighbourly.composeapp.generated.resources.image
import neighbourly.composeapp.generated.resources.info
import neighbourly.composeapp.generated.resources.need
import neighbourly.composeapp.generated.resources.reminder
import neighbourly.composeapp.generated.resources.request
import neighbourly.composeapp.generated.resources.sale
import neighbourly.composeapp.generated.resources.skillshare
import org.jetbrains.compose.resources.painterResource

@Composable
fun OrganismItemCard(
    item: ItemVS,
    onClick: () -> Unit,
) {
    val imgTag = painterResource(Res.drawable.image)
    val fileTag = painterResource(Res.drawable.file)
    val expTag = painterResource(Res.drawable.hourglass)

    val defaultItemImg = when (item.type) {
        ItemTypeVS.INFO -> painterResource(Res.drawable.info)
        ItemTypeVS.DONATION -> painterResource(Res.drawable.donate)
        ItemTypeVS.BARTER -> painterResource(Res.drawable.barter)
        ItemTypeVS.SALE -> painterResource(Res.drawable.sale)
        ItemTypeVS.EVENT -> painterResource(Res.drawable.event)
        ItemTypeVS.NEED -> painterResource(Res.drawable.need)
        ItemTypeVS.REQUEST -> painterResource(Res.drawable.request)
        ItemTypeVS.SKILLSHARE -> painterResource(Res.drawable.skillshare)
        ItemTypeVS.REMINDER -> painterResource(Res.drawable.reminder)
    }

    Card(
        modifier = Modifier.fillMaxWidth().clickable {
            onClick()
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
                            resource = { asyncPainterResource(data = item.imageUrl) },
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
                    FriendlyText(text = item.name, bold = true, fontSize = 20.sp)
                    FriendlyText(text = item.description, fontSize = 14.sp)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (item.imgCount > 0) {
                    ItemBadge(imgTag, item.imgCount.toString())
                }

                if (item.fileCount > 0) {
                    ItemBadge(fileTag, item.fileCount.toString())
                }

                if (item.expLabel != null) {
                    ItemBadge(expTag, item.expLabel, AppColors.complementary)
                }

                Spacer(modifier = Modifier.weight(1f))

                ItemHouseholdBadge(item.householdImage, item.householdName)
            }
        }
    }
}