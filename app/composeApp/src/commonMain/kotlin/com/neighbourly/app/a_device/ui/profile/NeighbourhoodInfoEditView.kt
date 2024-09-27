package com.neighbourly.app.a_device.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.neighbourly.app.KoinProvider
import com.neighbourly.app.a_device.ui.AppColors
import com.neighbourly.app.a_device.ui.CurlyButton
import com.neighbourly.app.a_device.ui.CurlyText
import com.neighbourly.app.b_adapt.viewmodel.navigation.NavigationViewModel
import com.neighbourly.app.b_adapt.viewmodel.profile.NeighbourhoodInfoViewModel
import com.neighbourly.app.generateQrCode
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.addperson
import neighbourly.composeapp.generated.resources.cannot_create_neighbourhood
import neighbourly.composeapp.generated.resources.create
import neighbourly.composeapp.generated.resources.create_neighbourhood
import neighbourly.composeapp.generated.resources.draw_neighbourhood
import neighbourly.composeapp.generated.resources.join_neighbourhood
import neighbourly.composeapp.generated.resources.list_neighbourhoods
import neighbourly.composeapp.generated.resources.need_to_have_household
import neighbourly.composeapp.generated.resources.neighbourhoodName
import neighbourly.composeapp.generated.resources.no_neighbourhood
import neighbourly.composeapp.generated.resources.save
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun NeighbourhoodInfoEditView(
    navigationViewModel: NavigationViewModel = viewModel { KoinProvider.KOIN.get<NavigationViewModel>() },
    viewModel: NeighbourhoodInfoViewModel = viewModel { KoinProvider.KOIN.get<NeighbourhoodInfoViewModel>() },
) {
    val state by viewModel.state.collectAsState()

    if (state.drawing) {
        CurlyText(text = stringResource(Res.string.draw_neighbourhood))

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = state.nameOverride ?: state.name,
            enabled = state.drawingDone,
            onValueChange = {
                viewModel.updateName(it)
            },
            label = { Text(stringResource(Res.string.neighbourhoodName)) },
            isError = state.nameError,
        )

        Spacer(modifier = Modifier.height(8.dp))

        CurlyButton(text = stringResource(Res.string.save), loading = state.saving) {
            viewModel.onSaveNeighbourhood()
        }
    } else if (!state.hasLocalizedHouse) {
        CurlyText(text = stringResource(Res.string.need_to_have_household))
    } else {
        if (state.hasNeighbourhoods) {
            Spacer(modifier = Modifier.height(8.dp))

            CurlyText(text = stringResource(Res.string.list_neighbourhoods))

            Spacer(modifier = Modifier.height(8.dp))

            state.neighbourhoods.toList().forEach { (id, name) ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    CurlyText(text = "* " + name, bold = true)

                    Spacer(modifier = Modifier.width(8.dp))

                    Image(
                        painter = painterResource(Res.drawable.addperson),
                        contentDescription = "Household",
                        contentScale = ContentScale.FillBounds,
                        colorFilter = ColorFilter.tint(AppColors.primary),
                        modifier =
                            Modifier.size(36.dp).clickable {
                                navigationViewModel.goToScanMemberHouseholdForNeighbourhood(id)
                            },
                    )
                }
            }
        } else {
            CurlyText(text = stringResource(Res.string.no_neighbourhood))
        }

        Spacer(modifier = Modifier.height(8.dp))

        CurlyText(text = stringResource(Res.string.join_neighbourhood))

        state.userQr.takeIf { !it.isNullOrBlank() }?.let {
            Image(
                painter = BitmapPainter(generateQrCode(it, 400)),
                contentDescription = "QR Code",
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(16.dp),
            )
        }
        if (state.isHouseholdHead) {
            CurlyText(text = stringResource(Res.string.create_neighbourhood))

            Spacer(modifier = Modifier.height(8.dp))

            CurlyButton(text = stringResource(Res.string.create)) {
                viewModel.createNeighbourhood()
                navigationViewModel.goToMap()
            }
        } else {
            CurlyText(text = stringResource(Res.string.cannot_create_neighbourhood))
        }
    }
}
