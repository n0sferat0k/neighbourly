package com.neighbourly.app.b_adapt.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neighbourly.app.c_business.usecase.profile.HouseholdLocalizeUseCase
import com.neighbourly.app.c_business.usecase.profile.NeighbourhoodManagementUseCase
import com.neighbourly.app.d_entity.data.GpsItem
import com.neighbourly.app.d_entity.data.Household
import com.neighbourly.app.d_entity.data.ItemType
import com.neighbourly.app.d_entity.interf.Db
import com.neighbourly.app.d_entity.interf.SessionStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MapViewModel(
    val sessionStore: SessionStore,
    val database: Db,
    val householdLocalizeUseCase: HouseholdLocalizeUseCase,
    val neighbourhoodManagementUseCase: NeighbourhoodManagementUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(MapViewState())
    val state: StateFlow<MapViewState> = _state.asStateFlow()

    init {
        sessionStore.isLoggedInFlow.onEach {
            if (!it) {
                _state.update { MapViewState() }
            }
        }.launchIn(viewModelScope)

        sessionStore.localizationFlow
            .onEach { localization ->
                _state.update {
                    it.copy(
                        drawing = localization.drawing,
                        heatmap =
                        localization.heatmap?.map {
                            GpsItemVS(
                                latitude = it.latitude,
                                longitude = it.longitude,
                                frequency = it.frequency ?: 1,
                            )
                        },
                        candidate =
                        localization.candidate?.let {
                            GpsItemVS(
                                latitude = it.latitude,
                                longitude = it.longitude,
                            )
                        },
                    )
                }
            }.launchIn(viewModelScope)

        sessionStore.userFlow
            .onEach { user ->
                if (user?.localizing == true) {
                    runCatching {
                        householdLocalizeUseCase.fetchGpsLogs()
                        householdLocalizeUseCase.fetchGpsCandidate()
                    }
                }
                val ownHousehold = user?.household?.let { household ->
                    household.toHouseholdVS(_state.value.candidate, true).let {
                        if (household.location == null) {
                            it?.copy(name = household.name + "<br />[CANDIDATE]")
                        } else {
                            it
                        }
                    }
                }
                _state.update {
                    it.copy(
                        lastSyncTs = user?.lastSyncTs ?: 0,
                        myHousehold = ownHousehold?.pullStatsClone(),
                        neighbourhoods = user?.neighbourhoods?.map {
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

    fun onDrawn(drawData: List<List<Float>>) {
        viewModelScope.launch {
            neighbourhoodManagementUseCase.saveDrawing(
                drawData.map { GpsItem(latitude = it[0], longitude = it[1]) },
            )
        }
    }

    fun refreshMapContent() {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                val filteredHouses = database.filterHouseholds()

                val houses = filteredHouses.filter { it.location != null }.map {
                    it.toHouseholdVS()?.pullStatsClone()
                }.filterNotNull().toMutableList()

                withContext(Dispatchers.Main) {
                    _state.update {
                        it.copy(
                            otherHouseholds = houses
                        )
                    }
                }
            }
        }
    }

    private suspend fun HouseholdVS.pullStatsClone(): HouseholdVS {
        val items = database.filterItems(householdId = id)
        return this.copy(
            skillshare = items.filter { it.type == ItemType.SKILLSHARE }.size,
            requests = items.filter { it.type == ItemType.REQUEST }.size,
            needs = items.filter { it.type == ItemType.NEED }.size,
            events = items.filter { it.type == ItemType.EVENT }.size,
            sales = items.filter { it.type == ItemType.SALE }.size,
            barterings = items.filter { it.type == ItemType.BARTER }.size,
            donations = items.filter { it.type == ItemType.DONATION }.size
        )
    }

    data class MapViewState(
        val drawing: Boolean = false,
        val lastSyncTs: Int = 0,
        val myHousehold: HouseholdVS? = null,
        val otherHouseholds: List<HouseholdVS> = emptyList(),
        val neighbourhoods: List<NeighbourhoodVS> = emptyList(),
        val heatmap: List<GpsItemVS>? = null,
        val candidate: GpsItemVS? = null,
    )

    data class HouseholdVS(
        val id: Int,
        val location: GpsItemVS,
        val name: String,
        val floatName: Boolean = false,
        val address: String = "",
        val description: String = "",
        val skillshare: Int = 0,
        val requests: Int = 0,
        val needs: Int = 0,
        val events: Int = 0,
        val sales: Int = 0,
        val barterings: Int = 0,
        val donations: Int = 0,
        val imageurl: String? = null,
    )

    data class NeighbourhoodVS(
        val id: Int,
        val name: String,
        val geofence: String,
    )

    data class GpsItemVS(
        val latitude: Float,
        val longitude: Float,
        val frequency: Int = 1,
    )

    fun Pair<Float, Float>.toGpsItemVS() =
        GpsItemVS(
            first,
            second,
        )

    fun Household.toHouseholdVS(
        alternateLocation: GpsItemVS? = null,
        floatName: Boolean = false
    ): HouseholdVS? =
        (location?.let { GpsItemVS(it.first, it.second) } ?: alternateLocation)?.let { location ->
            HouseholdVS(
                id = householdid,
                location = location,
                name = name,
                floatName = floatName,
                address = address.orEmpty(),
                description = about.orEmpty(),
                imageurl = imageurl,
            )
        }

}
