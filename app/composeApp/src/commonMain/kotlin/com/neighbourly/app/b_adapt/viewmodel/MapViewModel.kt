package com.neighbourly.app.b_adapt.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neighbourly.app.c_business.usecase.profile.HouseholdLocalizeUseCase
import com.neighbourly.app.d_entity.interf.SessionStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

class MapViewModel(
    val sessionStore: SessionStore,
    val householdLocalizeUseCase: HouseholdLocalizeUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(MapViewState())
    val state: StateFlow<MapViewState> = _state.asStateFlow()

    init {
        sessionStore.heatmap
            .onEach { heatmap ->
                _state.update {
                    it.copy(
                        heatmap =
                            heatmap?.map {
                                HeatmapItemVS(
                                    latitude = it.latitude,
                                    longitude = it.longitude,
                                    frequency = it.frequency,
                                )
                            },
                    )
                }
            }.launchIn(viewModelScope)

        sessionStore.user
            .onEach { user ->
                if (user?.localizing == true) {
                    householdLocalizeUseCase.fetchGpsLogs()
                }
                _state.update {
                    it.copy(
                        household =
                            user?.household?.location?.let {
                                HouseholdVS(
                                    id = user.household.householdid,
                                    latitude = it.first,
                                    longitude = it.second,
                                    name = user.household.name,
                                    imageurl = user.household.imageurl,
                                )
                            },
                        neighbourhoods =
                            user?.neighbourhoods?.map {
                                NeighbourhoodVS(
                                    id = it.neighbourhoodid,
                                    name = it.name,
                                    geofence = it.geofence,
                                )
                            } ?: emptyList(),
                    )
                }
            }.launchIn(viewModelScope)
    }

    fun onDrawn(drawData: List<List<Double>>) {
    }

    fun onHouseholSelected(householdid: Int) {
    }

    data class MapViewState(
        val household: HouseholdVS? = null,
        val neighbourhoods: List<NeighbourhoodVS> = emptyList(),
        val heatmap: List<HeatmapItemVS>? = null,
    )

    data class HouseholdVS(
        val id: Int,
        val latitude: Float,
        val longitude: Float,
        val name: String,
        val imageurl: String? = null,
    )

    data class NeighbourhoodVS(
        val id: Int,
        val name: String,
        val geofence: String,
    )

    data class HeatmapItemVS(
        val latitude: Float,
        val longitude: Float,
        val frequency: Int,
    )
}
