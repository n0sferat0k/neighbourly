package com.neighbourly.app.b_adapt.viewmodel.navigation

import com.neighbourly.app.b_adapt.viewmodel.navigation.MainContent.MainMenu
import com.neighbourly.app.b_adapt.viewmodel.navigation.ProfileContent.ProfileInfoEdit
import com.neighbourly.app.b_adapt.viewmodel.navigation.WebContent.WebMap
import com.neighbourly.app.d_entity.data.ItemType

data class NavigationViewState(
    val userLoggedIn: Boolean = false,
    val disableMainToggle: Boolean = false,
    val restrictedContent: Boolean = true,
    val mainContentVisible: Boolean = true,
    val addingNewHousehold: Boolean = false,
    val mainContent: MainContent = MainMenu,
    val profileContent: ProfileContent = ProfileInfoEdit,
    val webContent: WebContent = WebMap,
)

sealed interface WebContent {
    object WebMap : WebContent
    data class WebGallery(val itemId: Int, val imageId: Int) : WebContent
}

sealed interface MainContent {
    object MainMenu : MainContent

    object ManageProfile : MainContent
    object ManageMyStuff : MainContent
    object PublishStuff : MainContent

    data class ShowItemDetails(val itemId: Int) : MainContent

    data class FindItems(
        val type: ItemType? = null,
        val householdId: Int? = null
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