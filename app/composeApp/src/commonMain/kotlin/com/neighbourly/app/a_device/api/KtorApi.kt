package com.neighbourly.app.a_device.api

import com.neighbourly.app.b_adapt.gateway.api.AddMemberToHouseholdInput
import com.neighbourly.app.b_adapt.gateway.api.AddMemberToNeighbourhoodInput
import com.neighbourly.app.b_adapt.gateway.api.ApiException
import com.neighbourly.app.b_adapt.gateway.api.AttachmentDTO
import com.neighbourly.app.b_adapt.gateway.api.BoxDTO
import com.neighbourly.app.b_adapt.gateway.api.FetchProfileInput
import com.neighbourly.app.b_adapt.gateway.api.GpsItemDTO
import com.neighbourly.app.b_adapt.gateway.api.GpsLogInput
import com.neighbourly.app.b_adapt.gateway.api.ItemDTO
import com.neighbourly.app.b_adapt.gateway.api.ItemMessageDTO
import com.neighbourly.app.b_adapt.gateway.api.LoginInput
import com.neighbourly.app.b_adapt.gateway.api.NeighbourhoodDTO
import com.neighbourly.app.b_adapt.gateway.api.RegisterInput
import com.neighbourly.app.b_adapt.gateway.api.ResetInput
import com.neighbourly.app.b_adapt.gateway.api.SyncResponseDTO
import com.neighbourly.app.b_adapt.gateway.api.UpdateHouseholdInput
import com.neighbourly.app.b_adapt.gateway.api.UpdateNeighbourhoodInput
import com.neighbourly.app.b_adapt.gateway.api.UpdateProfileInput
import com.neighbourly.app.b_adapt.gateway.api.UserDTO
import com.neighbourly.app.d_entity.data.FileContents
import com.neighbourly.app.httpClientEngine
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object KtorApi {
    private val client = HttpClient(httpClientEngine) {
        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    println("HTTP Client " + message)
                }
            }
            level = LogLevel.BODY
        }
        install(ContentNegotiation) {
            json(
                Json {
                    encodeDefaults = true
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                },
                contentType = ContentType.Any,
            )
        }
    }

    suspend fun register(
        baseUrl: String,
        registerInput: RegisterInput,
    ): UserDTO {
        val response: HttpResponse = client.post(baseUrl + "register") {
            contentType(ContentType.Application.Json)
            setBody(registerInput)
        }
        if (response.status.value == 200) {
            return response.body<UserDTO>()
        } else {
            throw ApiException(response.bodyAsText())
        }
    }

    suspend fun logout(
        baseUrl: String,
        token: String,
        logoutAll: Boolean,
    ) {
        client.post(baseUrl + "logout") {
            parameter("logoutAll", logoutAll)
            headers {
                append(HttpHeaders.Authorization, "Bearer " + token)
            }
        }
    }

    suspend fun login(
        baseUrl: String,
        loginInput: LoginInput,
    ): UserDTO {
        val response: HttpResponse = client.post(baseUrl + "login") {
            contentType(ContentType.Application.Json)
            setBody(loginInput)
        }
        if (response.status.value == 200) {
            return response.body<UserDTO>()
        } else {
            throw ApiException(response.bodyAsText())
        }
    }

    suspend fun reset(
        baseUrl: String,
        resetInput: ResetInput,
    ) {
        val response: HttpResponse = client.post(baseUrl + "forgot") {
            contentType(ContentType.Application.Json)
            setBody(resetInput)
        }
        if (response.status.value != 200) {
            throw ApiException(response.bodyAsText())
        }
    }

    suspend fun updateProfile(
        baseUrl: String,
        token: String,
        updateProfileInput: UpdateProfileInput,
    ): UserDTO {
        val response: HttpResponse = client.post(baseUrl + "profile/update") {
            contentType(ContentType.Application.Json)
            setBody(updateProfileInput)
            headers {
                append(HttpHeaders.Authorization, "Bearer " + token)
            }
        }
        if (response.status.value == 200) {
            return response.body<UserDTO>()
        } else {
            throw ApiException(response.bodyAsText())
        }
    }

    suspend fun updateHousehold(
        baseUrl: String,
        token: String,
        updateHouseholdInput: UpdateHouseholdInput,
    ): UserDTO {
        val response: HttpResponse = client.post(baseUrl + "profile/updateHousehold") {
            contentType(ContentType.Application.Json)
            setBody(updateHouseholdInput)
            headers {
                append(HttpHeaders.Authorization, "Bearer " + token)
            }
        }
        if (response.status.value == 200) {
            return response.body<UserDTO>()
        } else {
            throw ApiException(response.bodyAsText())
        }
    }

    suspend fun leaveHousehold(
        baseUrl: String,
        token: String,
    ): UserDTO {
        val response: HttpResponse = client.post(baseUrl + "profile/leaveHousehold") {
            contentType(ContentType.Application.Json)
            headers {
                append(HttpHeaders.Authorization, "Bearer " + token)
            }
        }
        if (response.status.value == 200) {
            return response.body<UserDTO>()
        } else {
            throw ApiException(response.bodyAsText())
        }
    }

    suspend fun addMemberToHousehold(
        baseUrl: String,
        token: String,
        addMemberToHouseholdInput: AddMemberToHouseholdInput,
    ): UserDTO {
        val response: HttpResponse = client.post(baseUrl + "profile/addToHousehold") {
            contentType(ContentType.Application.Json)
            setBody(addMemberToHouseholdInput)
            headers {
                append(HttpHeaders.Authorization, "Bearer " + token)
            }
        }
        if (response.status.value == 200) {
            return response.body<UserDTO>()
        } else {
            throw ApiException(response.bodyAsText())
        }
    }

    suspend fun addMemberToNeighbourhood(
        baseUrl: String,
        token: String,
        addMemberToNeighbourhoodInput: AddMemberToNeighbourhoodInput,
    ) {
        val response: HttpResponse = client.post(baseUrl + "profile/addToNeighbourhood") {
            contentType(ContentType.Application.Json)
            setBody(addMemberToNeighbourhoodInput)
            headers {
                append(HttpHeaders.Authorization, "Bearer " + token)
            }
        }
        if (response.status.value != 200) {
            throw ApiException(response.bodyAsText())
        }
    }

    suspend fun updateNeighbourhood(
        baseUrl: String,
        token: String,
        updateNeighbourhoodInput: UpdateNeighbourhoodInput,
    ): UserDTO {
        val response: HttpResponse = client.post(baseUrl + "profile/updateNeighbourhood") {
            contentType(ContentType.Application.Json)
            setBody(updateNeighbourhoodInput)
            headers {
                append(HttpHeaders.Authorization, "Bearer " + token)
            }
        }
        if (response.status.value == 200) {
            return response.body<UserDTO>()
        } else {
            throw ApiException(response.bodyAsText())
        }
    }

    suspend fun leaveNeighbourhood(
        baseUrl: String,
        token: String,
        neighbourhoodId: Int,
    ): UserDTO {
        val response: HttpResponse = client.post(baseUrl + "profile/leaveNeighbourhood") {
            contentType(ContentType.Application.Json)
            setBody(NeighbourhoodDTO(neighbourhoodid = neighbourhoodId))
            headers {
                append(HttpHeaders.Authorization, "Bearer " + token)
            }
        }
        if (response.status.value == 200) {
            return response.body<UserDTO>()
        } else {
            throw ApiException(response.bodyAsText())
        }
    }


    suspend fun refreshProfile(
        baseUrl: String,
        token: String,
    ): UserDTO {
        val response: HttpResponse = client.post(baseUrl + "profile/refresh") {
            headers {
                append(HttpHeaders.Authorization, "Bearer " + token)
            }
        }
        if (response.status.value == 200) {
            return response.body<UserDTO>()
        } else {
            throw ApiException(response.bodyAsText())
        }
    }

    suspend fun fetchProfile(
        baseUrl: String,
        token: String,
        fetchProfileInput: FetchProfileInput,
    ): UserDTO {
        val response: HttpResponse = client.post(baseUrl + "profile/fetch") {
            contentType(ContentType.Application.Json)
            setBody(fetchProfileInput)
            headers {
                append(HttpHeaders.Authorization, "Bearer " + token)
            }
        }
        if (response.status.value == 200) {
            return response.body<UserDTO>()
        } else {
            throw ApiException(response.bodyAsText())
        }
    }

    suspend fun uploadFile(
        baseUrl: String,
        token: String,
        target: String,
        targetId: String = "",
        fileContents: FileContents,
    ): AttachmentDTO {
        val response: HttpResponse = client.submitFormWithBinaryData(
            url = baseUrl + "files/upload?target=" + target + "&targetId=" + targetId,
            formData = formData {
                append(
                    "file",
                    fileContents.content,
                    Headers.build {
                        append(HttpHeaders.ContentType, fileContents.type)
                        append(
                            HttpHeaders.ContentDisposition,
                            "filename=\"${fileContents.name}\"",
                        )
                    },
                )
            },
        ) {
            headers {
                append(HttpHeaders.Authorization, "Bearer " + token)
            }
        }

        if (response.status.value == 201) {
            return response.body<AttachmentDTO>()
        } else {
            throw ApiException(response.bodyAsText())
        }
    }

    suspend fun deleteFile(
        baseUrl: String,
        token: String,
        target: String,
        targetId: String = "",
    ) {
        val response =
            client.get(baseUrl + "files/delete?target=" + target + "&targetId=" + targetId) {
                headers {
                    append(HttpHeaders.Authorization, "Bearer " + token)
                }
            }
        if (response.status.value != 200) {
            throw ApiException(response.bodyAsText())
        }
    }

    suspend fun gpsLog(
        baseUrl: String,
        token: String,
        gpsLogInput: GpsLogInput,
    ) {
        val response = client.post(baseUrl + "gps/log") {
            contentType(ContentType.Application.Json)
            setBody(gpsLogInput)
            headers {
                append(HttpHeaders.Authorization, "Bearer " + token)
            }
        }
        if (response.status.value != 200) {
            throw ApiException(response.bodyAsText())
        }
    }

    suspend fun getGpsHeatmap(
        baseUrl: String,
        token: String,
    ): List<GpsItemDTO>? {
        val response = client.get(baseUrl + "gps/heatmap?onlyNight=false") {
            headers {
                append(HttpHeaders.Authorization, "Bearer " + token)
            }
        }

        if (response.status.value == 200) {
            return response.body<List<GpsItemDTO>?>()
        } else {
            throw ApiException(response.bodyAsText())
        }
    }

    suspend fun getGpsCandidate(
        baseUrl: String,
        token: String,
    ): GpsItemDTO {
        val response = client.get(baseUrl + "gps/candidate") {
            headers {
                append(HttpHeaders.Authorization, "Bearer " + token)
            }
        }
        if (response.status.value == 200) {
            return response.body<GpsItemDTO>()
        } else {
            throw ApiException(response.bodyAsText())
        }
    }

    suspend fun acceptGpsCandidate(
        baseUrl: String,
        token: String,
    ): GpsItemDTO {
        val response = client.post(baseUrl + "gps/acceptCandidate") {
            headers {
                append(HttpHeaders.Authorization, "Bearer " + token)
            }
        }
        if (response.status.value == 200) {
            return response.body<GpsItemDTO>()
        } else {
            throw ApiException(response.bodyAsText())
        }
    }

    suspend fun clearGpsData(
        baseUrl: String,
        token: String,
    ) {
        val response = client.get(baseUrl + "gps/clear") {
            headers {
                append(HttpHeaders.Authorization, "Bearer " + token)
            }
        }
        if (response.status.value != 200) {
            throw ApiException(response.bodyAsText())
        }
    }

    suspend fun resetHouseholdLocation(
        baseUrl: String,
        token: String,
    ) {
        val response = client.get(baseUrl + "gps/resetHouseholdLocation") {
            headers {
                append(HttpHeaders.Authorization, "Bearer " + token)
            }
        }
        if (response.status.value != 200) {
            throw ApiException(response.bodyAsText())
        }
    }

    suspend fun synchronizeContent(
        baseUrl: String,
        token: String,
        lastSyncTs: Int,
    ): SyncResponseDTO {
        val response = client.get(baseUrl + "content/sync") {
            headers {
                append(HttpHeaders.Authorization, "Bearer " + token)
                append(HttpHeaders.IfModifiedSince, lastSyncTs.toString())
            }
        }
        if (response.status.value == 200) {
            return response.body<SyncResponseDTO>()
        } else {
            throw ApiException(response.bodyAsText())
        }
    }

    suspend fun deleteItem(
        baseUrl: String, token: String, itemId: Int
    ) {
        val response = client.get(baseUrl + "content/delItem?itemId=" + itemId) {
            headers {
                append(HttpHeaders.Authorization, "Bearer " + token)
            }
        }
        if (response.status.value != 200) {
            throw ApiException(response.bodyAsText())
        }
    }

    suspend fun addOrUpdateItem(
        baseUrl: String, token: String, item: ItemDTO
    ): ItemDTO {
        val response = client.post(baseUrl + "content/addOrUpdateItem") {
            contentType(ContentType.Application.Json)
            setBody(item)
            headers {
                append(HttpHeaders.Authorization, "Bearer " + token)
            }
        }
        if (response.status.value == 200) {
            return response.body<ItemDTO>()
        } else {
            throw ApiException(response.bodyAsText())
        }
    }

    suspend fun addItemMessage(
        baseUrl: String,
        token: String,
        message: ItemMessageDTO
    ): ItemMessageDTO {
        val response = client.post(baseUrl + "content/addItemMessage") {
            contentType(ContentType.Application.Json)
            setBody(message)
            headers {
                append(HttpHeaders.Authorization, "Bearer " + token)
            }
        }
        if (response.status.value == 200) {
            return response.body<ItemMessageDTO>()
        } else {
            throw ApiException(response.bodyAsText())
        }
    }

    suspend fun getItemsMessages(
        baseUrl: String,
        token: String,
        itemIds: List<Int>,
        lastSyncTs: Int
    ): List<ItemMessageDTO> {
        val response = client.post(
            baseUrl + "content/getItemsMessages"
        ) {
            contentType(ContentType.Application.Json)
            setBody(itemIds)
            headers {
                append(HttpHeaders.Authorization, "Bearer " + token)
                append(HttpHeaders.IfModifiedSince, lastSyncTs.toString())
            }
        }
        if (response.status.value == 200) {
            return response.body<List<ItemMessageDTO>>()
        } else {
            throw ApiException(response.bodyAsText())
        }
    }

    suspend fun deleteItemMessage(baseUrl: String, token: String, itemMessageId: Int) {
        val response =
            client.get(baseUrl + "content/deleteItemMessage?itemMessageId=$itemMessageId") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer " + token)
                }
            }
        if (response.status.value != 200) {
            throw ApiException(response.bodyAsText())
        }
    }

    suspend fun boxAdd(baseUrl: String, token: String, box: BoxDTO) {
        val response = client.post(baseUrl + "box/addBox") {
            contentType(ContentType.Application.Json)
            setBody(box)
            headers {
                append(HttpHeaders.Authorization, "Bearer " + token)
            }
        }
        if (response.status.value != 200) {
            throw ApiException(response.bodyAsText())
        }
    }

    suspend fun boxDel(baseUrl: String, token: String, box: BoxDTO) {
        val response = client.post(baseUrl + "box/delBox") {
            contentType(ContentType.Application.Json)
            setBody(box)
            headers {
                append(HttpHeaders.Authorization, "Bearer " + token)
            }
        }
        if (response.status.value != 200) {
            throw ApiException(response.bodyAsText())
        }
    }

    suspend fun boxOp(baseUrl: String, token: String, box: BoxDTO) {
        val response = client.post(baseUrl + "box/opBox") {
            contentType(ContentType.Application.Json)
            setBody(box)
            headers {
                append(HttpHeaders.Authorization, "Bearer " + token)
            }
        }
        if (response.status.value != 200) {
            throw ApiException(response.bodyAsText())
        }
    }
}
