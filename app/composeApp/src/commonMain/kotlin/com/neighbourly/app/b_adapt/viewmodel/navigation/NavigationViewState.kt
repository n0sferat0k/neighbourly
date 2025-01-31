package com.neighbourly.app.b_adapt.viewmodel.navigation

import com.neighbourly.app.b_adapt.viewmodel.bean.ItemTypeVS
import com.neighbourly.app.b_adapt.viewmodel.navigation.MainContent.MainMenu
import com.neighbourly.app.b_adapt.viewmodel.navigation.ProfileContent.ProfileInfoEdit
import com.neighbourly.app.b_adapt.viewmodel.navigation.WebContent.WebMap

data class NavigationViewState(
    val disableMainToggle: Boolean = false,
    val mainContentVisible: Boolean = true,
    val addingNewHousehold: Boolean = false,
    val mainContent: MainContent = MainMenu,
    val profileContent: ProfileContent = ProfileInfoEdit,
    val webContent: WebContent = WebMap,
)

sealed interface WebContent {
    object WebMap : WebContent
    data class WebGallery(val itemId: Int? = null, val householdId: Int? = null, val imageId: Int) : WebContent
    data class WebPage(val url: String) : WebContent
}

sealed interface MainContent {
    object MainMenu : MainContent

    object ManageProfile : MainContent
    object ManageMyStuff : MainContent
    data class PublishStuff(val type: ItemTypeVS? = null) : MainContent
    object BoxManage : MainContent
    object Reminders : MainContent

    object BackendInfo : MainContent
    object AiInterface : MainContent
    data class HouseholdDetails(val householdId: Int) : MainContent
    data class ItemDetails(val itemId: Int) : MainContent

    data class FindItems(
        val type: ItemTypeVS? = null,
        val householdId: Int? = null,
        val itemIds: List<Int>? = null,
    ) : MainContent
}

sealed class ProfileContent {
    object ProfileInfoEdit : ProfileContent()

    object HouseholdScanMember : ProfileContent()

    data class NeighbourhoodScanMember(
        val neighbourhoodid: Int,
    ) : ProfileContent()

    data class HouseholdAddMember(
        val id: Int,
        val username: String,
    ) : ProfileContent()

    data class NeighbourhoodAddMemberHousehold(
        val neighbourhoodid: Int,
        val id: Int,
        val username: String,
    ) : ProfileContent()

    object HouseholdInfoEdit : ProfileContent()

    object HouseholdLocalize : ProfileContent()

    object NeighbourhoodInfoEdit : ProfileContent()
}