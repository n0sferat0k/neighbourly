package com.neighbourly.app.a_device.ui.atomic.organism.item

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neighbourly.app.a_device.ui.atomic.atom.FriendlyIconedText
import com.neighbourly.app.a_device.ui.atomic.atom.FriendlyText
import com.neighbourly.app.a_device.ui.atomic.molecule.item.ImageGrid
import com.neighbourly.app.a_device.ui.atomic.molecule.item.ItemTypeOption
import com.neighbourly.app.a_device.ui.atomic.page.TYPE_ASSOC
import com.neighbourly.app.b_adapt.viewmodel.bean.ItemTypeVS
import com.neighbourly.app.b_adapt.viewmodel.bean.ItemTypeVS.REMINDER
import com.neighbourly.app.b_adapt.viewmodel.bean.ItemVS
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.dates
import neighbourly.composeapp.generated.resources.end_date
import neighbourly.composeapp.generated.resources.files
import neighbourly.composeapp.generated.resources.images
import neighbourly.composeapp.generated.resources.item_description
import neighbourly.composeapp.generated.resources.item_name
import neighbourly.composeapp.generated.resources.item_url
import neighbourly.composeapp.generated.resources.muted
import neighbourly.composeapp.generated.resources.newbadge
import neighbourly.composeapp.generated.resources.start_date
import neighbourly.composeapp.generated.resources.target_user
import neighbourly.composeapp.generated.resources.type
import neighbourly.composeapp.generated.resources.unknown
import neighbourly.composeapp.generated.resources.unmuted
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OrganismReadonlyItemDetails(
    item: ItemVS,
    users: Map<Int, String>,
    onImageSelected: (imageId: Int) -> Unit,
    onUrlSelected: (url: String) -> Unit,
    onWatchItem: (watched: Boolean) -> Unit
) {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FriendlyText(text = stringResource(Res.string.type), bold = true)
            FriendlyText(
                text = stringResource(
                    TYPE_ASSOC[item.type]?.second ?: Res.string.unknown
                ),
            )
            ItemTypeOption(
                icon = painterResource(
                    TYPE_ASSOC[item.type]?.first ?: Res.drawable.newbadge
                ),
                selected = false,
                contentDesc = item.type.name,
            ) {}
        }

        if (listOf(ItemTypeVS.NEED, ItemTypeVS.REQUEST).contains(item.type)
            && item.targetUserId != null
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                FriendlyText(text = stringResource(Res.string.target_user), bold = true)
                FriendlyText(text = users.getOrDefault(item.targetUserId, ""))
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FriendlyText(text = stringResource(Res.string.item_name), fontSize = 20.sp, bold = true)
            FriendlyIconedText(
                text = item.name,
                painter = painterResource(if (item.augmentation?.watched == false) Res.drawable.muted else Res.drawable.unmuted),
                bold = false,
                fontSize = 20.sp,
                iconSize = 36.dp,
                iconClick = { onWatchItem(!(item.augmentation?.watched ?: false)) }
            )
        }
        if (item.description.isNotEmpty() && item.type != REMINDER) {
            FriendlyText(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(Res.string.item_description), bold = true
            )
            FriendlyText(
                modifier = Modifier.fillMaxWidth(),
                text = item.description
            )
        }

        if (item.url.isNotEmpty()) {
            FriendlyText(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(Res.string.item_url), bold = true
            )
            FriendlyText(modifier = Modifier.fillMaxWidth().clickable {
                onUrlSelected(item.url)
            }, text = item.url)
        }

        if (item.images.isNotEmpty()) {
            FriendlyText(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(Res.string.images),
                bold = true
            )

            if (item.images.isNotEmpty()) {
                ImageGrid(
                    images = item.images,
                    newImages = emptyList()
                ) { image ->
                    onImageSelected(image.id)
                }
            }
        }

        if (item.files.isNotEmpty()) {
            FriendlyText(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(Res.string.files),
                bold = true
            )

            item.files.onEach {
                FriendlyText(
                    modifier = Modifier.fillMaxWidth()
                        .clickable {
                            onUrlSelected(it.url)
                        },
                    text = item.name, bold = true
                )
            }
        }

        if (item.dates.isNotEmpty() && item.type == REMINDER) {
            FriendlyText(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(Res.string.dates), bold = true
            )
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item.dates.forEach {
                    FriendlyText(
                        modifier = Modifier.fillMaxWidth(),
                        text = it.toLocalDateTime(TimeZone.currentSystemDefault())
                            .toJavaLocalDateTime().format(formatter),
                        fontSize = 14.sp
                    )
                }
            }
        }

        if (item.start != null && item.start.epochSeconds > 0) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                FriendlyText(text = stringResource(Res.string.start_date))
                FriendlyText(
                    text = item.start.toLocalDateTime(TimeZone.currentSystemDefault())
                        .toJavaLocalDateTime().format(formatter),
                    bold = true
                )
            }
        }

        if (item.end != null && item.end.epochSeconds > 0) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                FriendlyText(text = stringResource(Res.string.end_date))
                FriendlyText(
                    text = item.end.toLocalDateTime(TimeZone.currentSystemDefault())
                        .toJavaLocalDateTime().format(formatter),
                    bold = true
                )
            }
        }
    }
}