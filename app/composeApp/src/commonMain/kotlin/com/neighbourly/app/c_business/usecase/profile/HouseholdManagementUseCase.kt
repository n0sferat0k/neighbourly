package com.neighbourly.app.c_business.usecase.profile

import com.neighbourly.app.d_entity.data.FileContents
import com.neighbourly.app.d_entity.interf.Api
import com.neighbourly.app.d_entity.interf.SessionStore

class HouseholdManagementUseCase(
    val apiGw: Api,
    val sessionStore: SessionStore,
) {
    suspend fun updateInfo(
        name: String,
        address: String,
        about: String,
    ) {
        val token = sessionStore.user?.authtoken

        token?.let {
            val user = apiGw.updateHousehold(it, name, address, about)
            sessionStore.updateUser {
                it?.copy(
                    username = user.username,
                    about = user.about,
                    fullname = user.fullname,
                    email = user.email,
                    phone = user.phone,
                    imageurl = user.imageurl,
                    household = user.household,
                    neighbourhoods = user.neighbourhoods,
                )
            }
        }
    }

    suspend fun leaveHousehold() {
        val token = sessionStore.user?.authtoken

        token?.let {
            val user = apiGw.leaveHousehold(it)
            sessionStore.updateUser {
                it?.copy(
                    username = user.username,
                    about = user.about,
                    fullname = user.fullname,
                    email = user.email,
                    phone = user.phone,
                    imageurl = user.imageurl,
                    household = user.household,
                    neighbourhoods = user.neighbourhoods,
                    localizing = false,
                )
            }
        }
    }

    suspend fun addMember(
        id: Int,
        username: String,
        access: Map<Int, Int>,
    ) {
        val token = sessionStore.user?.authtoken

        token?.let {
            val user = apiGw.addMemberToHousehold(it, id, username, access)
            sessionStore.updateUser {
                it?.copy(
                    username = user.username,
                    about = user.about,
                    fullname = user.fullname,
                    email = user.email,
                    phone = user.phone,
                    imageurl = user.imageurl,
                    household = user.household,
                    neighbourhoods = user.neighbourhoods,
                )
            }
        }
    }

    suspend fun updateImage(profileImageFileContents: FileContents) {
        val token = sessionStore.user?.authtoken

        token?.let {
            apiGw.updateHouseholdImage(it, profileImageFileContents).let { imgUrl ->
                sessionStore.updateUser { it?.copy(household = it.household?.copy(imageurl = imgUrl)) }
            }
        }
    }


}
