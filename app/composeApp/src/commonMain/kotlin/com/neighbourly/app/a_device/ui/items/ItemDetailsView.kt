package com.neighbourly.app.a_device.ui.items

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.OutlinedTextField
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.neighbourly.app.KoinProvider
import com.neighbourly.app.a_device.ui.Alert
import com.neighbourly.app.a_device.ui.AppColors
import com.neighbourly.app.a_device.ui.BoxHeader
import com.neighbourly.app.a_device.ui.BoxScrollableContent
import com.neighbourly.app.a_device.ui.SwipeToDeleteBox
import com.neighbourly.app.b_adapt.viewmodel.items.ItemDetailsViewModel
import com.neighbourly.app.b_adapt.viewmodel.navigation.NavigationViewModel
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.confirm_deleteing_image
import neighbourly.composeapp.generated.resources.deleteing_image
import neighbourly.composeapp.generated.resources.item_description
import neighbourly.composeapp.generated.resources.item_name
import org.jetbrains.compose.resources.stringResource

@Composable
fun ItemDetailsView(
    itemId: Int? = null,
    viewModel: ItemDetailsViewModel = viewModel { KoinProvider.KOIN.get<ItemDetailsViewModel>() },
    navigationViewModel: NavigationViewModel = viewModel { KoinProvider.KOIN.get<NavigationViewModel>() }
) {
    val state by viewModel.state.collectAsState()
    val navigation by navigationViewModel.state.collectAsState()

    LaunchedEffect(itemId) {
        viewModel.setItem(itemId)
    }

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        BoxHeader(Modifier.align(Alignment.Start))

        BoxScrollableContent(modifier = Modifier.weight(1f)) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // Item title input
                OutlinedTextField(
                    value = state.nameOverride ?: state.name,
                    onValueChange = {
                        viewModel.updateName(it)
                    },
                    isError = state.nameError,
                    label = { Text(stringResource(Res.string.item_name)) },
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Item title input
                OutlinedTextField(
                    value = state.descriptionOverride ?: state.description,
                    onValueChange = {
                        viewModel.updateDescription(it)
                    },
                    maxLines = 5,
                    label = { Text(stringResource(Res.string.item_description)) },
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (state.images.size > 0) {
                    ImageGrid(state.images) {
                        viewModel.deleteImage(it)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ImageGrid(images: Map<Int, String>, delete: (Int) -> Unit) {
    var showRemoveAlertForId by remember { mutableStateOf(-1) }

    if (showRemoveAlertForId != -1) {
        Alert(
            title = stringResource(Res.string.deleteing_image),
            text = stringResource(Res.string.confirm_deleteing_image),
            ok = {
                showRemoveAlertForId = -1
                delete(showRemoveAlertForId)
            },
            cancel = {
                showRemoveAlertForId = -1
            }
        )
    }

    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        images.forEach { (key, imageUrl) ->
            SwipeToDeleteBox(modifier = Modifier.size(84.dp), onDelete = {
                showRemoveAlertForId = key
            }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f),
                    shape = RoundedCornerShape(4.dp),
                    elevation = 4.dp
                ) {
                    KamelImage(
                        modifier = Modifier.fillMaxSize(),
                        resource = asyncPainterResource(data = imageUrl),
                        contentDescription = "Item Image",
                        contentScale = ContentScale.Crop,
                        onLoading = { progress ->
                            CircularProgressIndicator(
                                progress = progress,
                                color = AppColors.primary,
                            )
                        },
                    )
                }
            }
        }
    }
}