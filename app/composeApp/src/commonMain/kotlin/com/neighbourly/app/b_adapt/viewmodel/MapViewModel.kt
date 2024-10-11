package com.neighbourly.app.b_adapt.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neighbourly.app.c_business.usecase.profile.HouseholdLocalizeUseCase
import com.neighbourly.app.c_business.usecase.profile.NeighbourhoodManagementUseCase
import com.neighbourly.app.d_entity.data.GpsItem
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
        sessionStore.localization
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

        sessionStore.user
            .onEach { user ->
                if (user?.localizing == true) {
                    runCatching {
                        householdLocalizeUseCase.fetchGpsLogs()
                        householdLocalizeUseCase.fetchGpsCandidate()
                    }
                }
                _state.update {
                    it.copy(
                        lastSyncTs = user?.lastSyncTs ?: 0,
                        household =
                        user?.household?.let { household ->
                            (household.location?.toGpsItemVS()
                                ?: _state.value.candidate)?.let { location ->
                                HouseholdVS(
                                    id = household.householdid,
                                    location = location,
                                    name = if (household.location == null)
                                        household.name + "<br />[CANDIDATE]"
                                    else
                                        household.name,
                                    floatName = true,
                                    address = household.address.orEmpty(),
                                    description = household.about.orEmpty(),
                                    imageurl = household.imageurl,
                                )
                            }
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

    fun onDrawn(drawData: List<List<Float>>) {
        viewModelScope.launch {
            neighbourhoodManagementUseCase.saveDrawing(
                drawData.map { GpsItem(latitude = it[0], longitude = it[1]) },
            )
        }
    }

    fun onHouseholdSelected(householdid: Int, byName: ItemType) {
        println("AAAAAAAAAAAAA")
    }

    fun refreshMapContent() {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                val filteredHouses = database.filterHouseholds()

                val houses = filteredHouses.filter { it.location != null }.map {
                    val items = database.filterItemsByHousehold(it.householdid)

                    HouseholdVS(
                        id = it.householdid,
                        location = it.location!!.let {
                            GpsItemVS(it.first, it.second)
                        },
                        name = it.name,
                        floatName = false,
                        address = it.address.orEmpty(),
                        description = it.about.orEmpty(),
                        imageurl = it.imageurl,
                        skillshare = items.filter { it.type == ItemType.SKILLSHARE }.size,
                        requests = items.filter { it.type == ItemType.REQUEST }.size,
                        needs = items.filter { it.type == ItemType.NEED }.size,
                        events = items.filter { it.type == ItemType.EVENT }.size,
                        sales = items.filter { it.type == ItemType.SALE }.size,
                        barterings = items.filter { it.type == ItemType.BARTER }.size,
                        donations = items.filter { it.type == ItemType.DONATION }.size
                    )
                }.toMutableList()

                _state.value.household?.let {
                    val items = database.filterItemsByHousehold(it.id)
                    houses.add(
                        it.copy(
                            skillshare = items.filter { it.type == ItemType.SKILLSHARE }.size,
                            requests = items.filter { it.type == ItemType.REQUEST }.size,
                            needs = items.filter { it.type == ItemType.NEED }.size,
                            events = items.filter { it.type == ItemType.EVENT }.size,
                            sales = items.filter { it.type == ItemType.SALE }.size,
                            barterings = items.filter { it.type == ItemType.BARTER }.size,
                            donations = items.filter { it.type == ItemType.DONATION }.size
                        )
                    )
                }

                withContext(Dispatchers.Main) {
                    _state.update {
                        it.copy(
                            houses = houses
                        )
                    }
                }
            }
        }
    }

    data class MapViewState(
        val drawing: Boolean = false,
        val lastSyncTs: Int = 0,
        val household: HouseholdVS? = null,
        val houses: List<HouseholdVS> = emptyList(),
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
}
