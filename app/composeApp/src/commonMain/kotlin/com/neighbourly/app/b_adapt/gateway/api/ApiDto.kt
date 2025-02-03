package com.neighbourly.app.b_adapt.gateway.api

import kotlinx.serialization.Serializable

@Serializable
data class LoginInput(
    val username: String,
    val password: String,
)

@Serializable
data class RegisterInput(
    val username: String,
    val password: String,
    val fullname: String,
    val phone: String,
    val email: String,
)

@Serializable
data class UpdateProfileInput(
    val fullname: String,
    val phone: String,
    val email: String,
    val about: String,
)

@Serializable
data class UpdateHouseholdInput(
    val name: String,
    val address: String,
    val about: String,
)

@Serializable
data class AddMemberToHouseholdInput(
    val id: Int,
    val username: String,
    val neighbourhoods: List<NeighbourhoodDTO>
)

@Serializable
data class AddMemberToNeighbourhoodInput(
    val neighbourhoodid: Int,
    val id: Int,
    val username: String,
    val accs: Map<Int, Int>?,
)

@Serializable
data class UpdateNeighbourhoodInput(
    val neighbourhoodid: Int? = null,
    val name: String,
    val geofence: String,
)

@Serializable
data class FetchProfileInput(
    val id: Int,
    val username: String,
)

@Serializable
data class GpsLogInput(
    val timezone: Int,
    val latitude: Float,
    val longitude: Float,
)

@Serializable
data class UserDTO(
    val id: Int,
    val username: String,
    val about: String? = null,
    val password: String? = null,
    val fullname: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val imageurl: String? = null,
    val authtoken: String? = null,
    val lastModifiedTs: Int = 0,
    val householdid: Int? = null,
    val household: HouseholdDTO? = null,
    val neighbourhoods: List<NeighbourhoodDTO> = emptyList(),
)

@Serializable
data class BoxDTO(
    val name: String = "",
    val id: String,
    val command: String? = null,
)

@Serializable
data class BoxCommandInputDTO(
    val id: String,
    val command: String,
)

@Serializable
data class GpsItemDTO(
    val latitude: Float,
    val longitude: Float,
    val frequency: Int? = null,
)

@Serializable
data class HouseholdDTO(
    val householdid: Int,
    val name: String,
    val headid: Int,
    val about: String? = null,
    val imageurl: String? = null,
    val latitude: Float? = null,
    val longitude: Float? = null,
    val address: String? = null,
    val gpsprogress: Float? = null,
    val lastModifiedTs: Int = 0,
    val members: List<UserDTO>? = null,
    val boxes: List<BoxDTO>? = null,
)

@Serializable
data class NeighbourhoodDTO(
    val neighbourhoodid: Int,
    val name: String? = null,
    val geofence: String? = null,
    val access: Int? = null,
    val parent: UserDTO? = null,
)

@Serializable
data class ItemDTO(
    val id: Int? = null,
    val type: String,
    val name: String? = null,
    val description: String? = null,
    val url: String? = null,
    val targetUserId: Int,
    val images: List<AttachmentDTO> = emptyList(),
    val files: List<AttachmentDTO> = emptyList(),
    val startTs: Int,
    val endTs: Int,
    val lastModifiedTs: Int? = null,
    val neighbourhoodId: Int? = null,
    val householdId: Int? = null,
    val userId: Int? = null,
)

@Serializable
data class ItemMessageDTO(
    val id: Int? = null,
    val lastModifiedTs: Int? = null,
    val message: String? = null,
    val userId: Int? = null,
    val itemId: Int? = null,
)

@Serializable
data class AttachmentDTO(
    val id: Int? = null,
    val url: String,
    val name: String = "",
)

@Serializable
data class SyncResponseDTO(
    val items: List<ItemDTO> = emptyList(),
    val itemIds: List<Int> = emptyList(),
    val users: List<UserDTO> = emptyList(),
    val userIds: List<Int> = emptyList(),
    val households: List<HouseholdDTO> = emptyList(),
    val householdIds: List<Int> = emptyList(),
)

class ApiException(
    val msg: String,
) : RuntimeException(msg)
