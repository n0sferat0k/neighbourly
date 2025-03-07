package com.neighbourly.app.b_adapt.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neighbourly.app.GeoLocationCallback
import com.neighbourly.app.GetLocation
import com.neighbourly.app.b_adapt.viewmodel.bean.GpsItemVS
import com.neighbourly.app.b_adapt.viewmodel.bean.HouseholdSummaryVS
import com.neighbourly.app.b_adapt.viewmodel.bean.ItemAugmentVS
import com.neighbourly.app.b_adapt.viewmodel.bean.ItemVS
import com.neighbourly.app.b_adapt.viewmodel.bean.NeighbourhoodVS
import com.neighbourly.app.b_adapt.viewmodel.bean.pullFrom
import com.neighbourly.app.b_adapt.viewmodel.bean.toGpsItemVS
import com.neighbourly.app.b_adapt.viewmodel.bean.toHouseholdSummaryVS
import com.neighbourly.app.b_adapt.viewmodel.bean.toItemVS
import com.neighbourly.app.b_adapt.viewmodel.bean.toNeighbourhoodVS
import com.neighbourly.app.c_business.usecase.profile.HouseholdLocalizeUseCase
import com.neighbourly.app.c_business.usecase.profile.NeighbourhoodManagementUseCase
import com.neighbourly.app.d_entity.data.GpsItem
import com.neighbourly.app.d_entity.data.ItemType
import com.neighbourly.app.d_entity.interf.Db
import com.neighbourly.app.d_entity.interf.SessionStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.min
import kotlin.random.Random

class WebMapViewModel(
    val sessionStore: SessionStore,
    val database: Db,
    val householdLocalizeUseCase: HouseholdLocalizeUseCase,
    val neighbourhoodManagementUseCase: NeighbourhoodManagementUseCase,
) : GeoLocationCallback, ViewModel() {
    private val _state = MutableStateFlow(MapViewState())
    val state: StateFlow<MapViewState> = _state.asStateFlow()
    val myRandom = Random(System.currentTimeMillis())

    init {
        //watch for log out and clear map on logout
        sessionStore.isLoggedInFlow.onEach {
            if (!it) {
                clearMapContent()
            }
        }.launchIn(viewModelScope)

        //watch for localization state and update drawing state, heatmap and candidate house
        sessionStore.localizationFlow
            .onEach { localization ->
                _state.update {
                    it.copy(
                        drawing = localization.drawing,
                        heatmap = localization.heatmap?.map {
                            GpsItemVS(
                                latitude = it.latitude,
                                longitude = it.longitude,
                                frequency = it.frequency ?: 1,
                            )
                        },
                        myHousehold = localization.candidate?.let { location ->
                            it.myHousehold.copy(
                                location = location.toGpsItemVS(),
                                isCandidate = true,
                            )

                        } ?: it.myHousehold,
                    )
                }
            }.launchIn(viewModelScope)

        //watch for user changes and update my house, other houses and neighbourhood
        sessionStore.userFlow
            .onEach { user ->
                val allOtherHouseholds =
                    database.filterHouseholds().filter { it.householdid != user?.householdid }.map {
                        it.toHouseholdSummaryVS().pullStatsClone()
                    }

                _state.update { state ->
                    state.copy(
                        myHousehold = user?.household?.let { state.myHousehold.pullFrom(it) }
                            ?: state.myHousehold,
                        neighbourhoods = user?.neighbourhoods?.map { it.toNeighbourhoodVS() }
                            ?: emptyList(),
                        otherHouseholds = allOtherHouseholds,
                        lastSync = user?.lastSyncTs,
                    )
                }
            }.launchIn(viewModelScope)
    }

    fun refreshRandomItems() {
        if (0 == myRandom.nextInt() % 3) {
            viewModelScope.launch {
                val randomItems =
                    database.filterItems(
                        ids = database.getItemIds().shuffled().let { items ->
                            items.subList(0, min(myRandom.nextInt(2), items.size))
                        })
                        .filter { it.householdId != _state.value.myHousehold.id && it.type != ItemType.REMINDER }
                        .distinctBy { it.householdId }


                val randomItemsByHouse = randomItems.map { item ->
                    _state.value.otherHouseholds.firstOrNull { it.id == item.householdId }
                        ?.let { house ->
                            house to
                                    item.toItemVS()
                                        .copy(augmentation = ItemAugmentVS(imageUrl = item.images.randomOrNull()?.url))
                        }
                }.filterNotNull().toMap()

                _state.update { it.copy(randomItems = randomItemsByHouse) }
            }
        } else {
            _state.update { it.copy(randomItems = emptyMap()) }
        }
    }

    override fun invoke(latitude: Double, longitude: Double, accuracy: Float) {
        _state.update {
            it.copy(
                myLocation = GpsItemVS(
                    latitude = latitude.toFloat(),
                    longitude = longitude.toFloat()
                )
            )
        }
    }

    fun onDrawn(drawData: List<List<Float>>) {
        viewModelScope.launch {
            neighbourhoodManagementUseCase.saveDrawing(
                drawData.map { GpsItem(latitude = it[0], longitude = it[1]) },
            )
        }
    }

    fun clearMapContent() {
        _state.update { MapViewState() }
    }

    private suspend fun HouseholdSummaryVS.pullStatsClone(): HouseholdSummaryVS {
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

    fun onMapReady(ready: Boolean) {
        _state.update { it.copy(mapReady = ready) }
        if (ready) {
            GetLocation.addCallback(this)
            if (sessionStore.user?.localizing == true) {
                viewModelScope.launch {
                    kotlin.runCatching {
                        householdLocalizeUseCase.fetchGpsLogs()
                        householdLocalizeUseCase.fetchGpsCandidate()
                    }
                }
            }
        } else {
            GetLocation.removeCallback(this)
        }
    }

    data class MapViewState(
        val mapReady: Boolean = false,
        val drawing: Boolean = false,
        val myLocation: GpsItemVS? = null,
        val myHousehold: HouseholdSummaryVS = HouseholdSummaryVS(),
        val otherHouseholds: List<HouseholdSummaryVS> = emptyList(),
        val neighbourhoods: List<NeighbourhoodVS> = emptyList(),
        val heatmap: List<GpsItemVS>? = null,
        val lastSync: Int? = null,
        val randomItems: Map<HouseholdSummaryVS, ItemVS> = emptyMap()
    )
}
