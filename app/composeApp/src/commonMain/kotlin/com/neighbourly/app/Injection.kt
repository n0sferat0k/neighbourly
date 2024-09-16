package com.neighbourly.app

import com.neighbourly.app.a_device.api.KtorAuthApi
import com.neighbourly.app.a_device.service.GpsTrackerImpl
import com.neighbourly.app.a_device.store.SessionHybridStore
import com.neighbourly.app.b_adapt.gateway.AuthApiGw
import com.neighbourly.app.b_adapt.viewmodel.MainViewModel
import com.neighbourly.app.b_adapt.viewmodel.MapViewModel
import com.neighbourly.app.b_adapt.viewmodel.auth.LoginViewModel
import com.neighbourly.app.b_adapt.viewmodel.auth.RegisterViewModel
import com.neighbourly.app.b_adapt.viewmodel.profile.HouseholdLocalizeViewModel
import com.neighbourly.app.b_adapt.viewmodel.profile.ProfileFooterViewModel
import com.neighbourly.app.b_adapt.viewmodel.profile.ProfileInfoEditViewModel
import com.neighbourly.app.b_adapt.viewmodel.profile.ProfileMenuViewModel
import com.neighbourly.app.b_adapt.viewmodel.profile.ProfileViewModel
import com.neighbourly.app.c_business.usecase.auth.LoginUseCase
import com.neighbourly.app.c_business.usecase.auth.LogoutUseCase
import com.neighbourly.app.c_business.usecase.auth.RegisterUseCase
import com.neighbourly.app.c_business.usecase.profile.HouseholdLocalizeUseCase
import com.neighbourly.app.c_business.usecase.profile.ProfileImageUpdateUseCase
import com.neighbourly.app.c_business.usecase.profile.ProfileRefreshUseCase
import com.neighbourly.app.c_business.usecase.profile.ProfileUpdateUseCase
import com.neighbourly.app.d_entity.interf.AuthApi
import com.neighbourly.app.d_entity.interf.GpsTracker
import com.neighbourly.app.d_entity.interf.KeyValueRegistry
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
                    deviceModule,
                    adapterModule,
                    useCaseModule,
                )
            }
    }
}

val deviceModule =
    module {
        single<SessionStore> {
            SessionHybridStore(get())
        }
        single<KeyValueRegistry> {
            keyValueRegistry
        }
        single<GpsTracker> {
            GpsTrackerImpl
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
            RegisterViewModel(get(), get())
        }
        factory {
            ProfileViewModel(get(), get(), get())
        }
        factory {
            ProfileMenuViewModel(get())
        }
        factory {
            ProfileInfoEditViewModel(get(), get())
        }
        factory {
            ProfileFooterViewModel(get())
        }
        factory {
            LoginViewModel(get())
        }
        factory {
            MapViewModel(get(), get())
        }
        factory {
            HouseholdLocalizeViewModel(get(), get())
        }
    }

val useCaseModule =
    module {
        single {
            LoginUseCase(get(), get())
        }
        single {
            LogoutUseCase(get(), get(), get())
        }
        single {
            RegisterUseCase(get(), get())
        }
        single {
            ProfileImageUpdateUseCase(get(), get())
        }
        single {
            ProfileRefreshUseCase(get(), get())
        }
        single {
            ProfileUpdateUseCase(get(), get())
        }
        single {
            HouseholdLocalizeUseCase(get(), get(), get())
        }
    }
