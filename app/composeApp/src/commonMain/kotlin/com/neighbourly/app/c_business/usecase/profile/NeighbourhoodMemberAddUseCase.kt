package com.neighbourly.app.c_business.usecase.profile

import com.neighbourly.app.d_entity.interf.Api
import com.neighbourly.app.d_entity.interf.SessionStore

class NeighbourhoodMemberAddUseCase(
    val apiGw: Api,
    val sessionStore: SessionStore,
) {
    suspend fun execute(
        neighbourhoodid: Int,
        id: Int,
        username: String,
        accs: Map<Int, Int>?,
    ) {
        val token = sessionStore.token

        token?.let {
            apiGw.addMemberToNeighbourhood(it, neighbourhoodid, id, username, accs)
        }
    }
}
