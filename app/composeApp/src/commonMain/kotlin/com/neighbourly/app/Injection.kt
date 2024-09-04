package com.neighbourly.app

import com.neighbourly.app.a_device.api.KtorAuthApi
import com.neighbourly.app.a_device.store.SessionMemoryStore
import com.neighbourly.app.b_adapt.gateway.AuthApiGw
import com.neighbourly.app.b_adapt.viewmodel.LoginViewModel
import com.neighbourly.app.b_adapt.viewmodel.MainViewModel
import com.neighbourly.app.b_adapt.viewmodel.ProfileViewModel
import com.neighbourly.app.b_adapt.viewmodel.RegisterViewModel
import com.neighbourly.app.c_business.usecase.LoginUseCase
import com.neighbourly.app.c_business.usecase.RegisterUseCase
import com.neighbourly.app.d_entity.interf.AuthApi
import com.neighbourly.app.d_entity.interf.SessionStore
import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

object KoinProvider {
    private lateinit var KOIN_APPLICATION: KoinApplication
    val KOIN: Koin
        get() = KOIN_APPLICATION.koin

    fun initKoin(config: KoinAppDeclaration? = null) {
        KOIN_APPLICATION =
            startKoin {
                config?.invoke(this)
                modules(
                    adapterModule,
                    useCaseModule,
                )
            }
    }
}

val adapterModule =
    module {
        single<AuthApi> {
            AuthApiGw(KtorAuthApi())
        }
        factory {
            MainViewModel(get())
        }
        factory {
            LoginViewModel(get())
        }
        factory {
            RegisterViewModel(get())
        }
        factory {
            ProfileViewModel(get())
        }
        single {
            LoginViewModel(get())
        }
        single<SessionStore> {
            SessionMemoryStore()
        }
    }

val useCaseModule =
    module {
        single {
            LoginUseCase(get(), get())
        }
        single {
            RegisterUseCase(get(), get())
        }
    }
