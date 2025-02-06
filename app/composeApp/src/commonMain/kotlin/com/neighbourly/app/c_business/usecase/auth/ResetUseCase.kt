package com.neighbourly.app.c_business.usecase.auth

import com.neighbourly.app.d_entity.interf.Api

class ResetUseCase(
    val apiGw: Api,
) {
    suspend fun execute(email: String) {
        apiGw.reset(email)
    }
}
