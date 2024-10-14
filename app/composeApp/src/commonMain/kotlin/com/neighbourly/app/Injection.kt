package com.neighbourly.app

import com.neighbourly.app.a_device.api.KtorApi
import com.neighbourly.app.a_device.store.SessionHybridStore
import com.neighbourly.app.b_adapt.gateway.ApiGateway
import com.neighbourly.app.b_adapt.interactor.DbInteractor
import com.neighbourly.app.b_adapt.viewmodel.MapViewModel
import com.neighbourly.app.b_adapt.viewmodel.auth.LoginViewModel
import com.neighbourly.app.b_adapt.viewmodel.auth.RegisterViewModel
import com.neighbourly.app.b_adapt.viewmodel.items.FilteredItemListViewModel
import com.neighbourly.app.b_adapt.viewmodel.navigation.NavigationViewModel
import com.neighbourly.app.b_adapt.viewmodel.profile.HouseholdAddMemberViewModel
import com.neighbourly.app.b_adapt.viewmodel.profile.HouseholdInfoEditViewModel
import com.neighbourly.app.b_adapt.viewmodel.profile.HouseholdLocalizeViewModel
import com.neighbourly.app.b_adapt.viewmodel.profile.NeighbourhoodAddMemberViewModel
import com.neighbourly.app.b_adapt.viewmodel.profile.NeighbourhoodInfoViewModel
import com.neighbourly.app.b_adapt.viewmodel.profile.ProfileFooterViewModel
import com.neighbourly.app.b_adapt.viewmodel.profile.ProfileInfoEditViewModel
import com.neighbourly.app.b_adapt.viewmodel.profile.ProfileMenuViewModel
import com.neighbourly.app.b_adapt.viewmodel.profile.ProfileViewModel
import com.neighbourly.app.c_business.usecase.auth.LoginUseCase
import com.neighbourly.app.c_business.usecase.auth.LogoutUseCase
import com.neighbourly.app.c_business.usecase.auth.RegisterUseCase
import com.neighbourly.app.c_business.usecase.items.ContentSyncUseCase
import com.neighbourly.app.c_business.usecase.profile.FetchProfileUseCase
import com.neighbourly.app.c_business.usecase.profile.HouseholdLocalizeUseCase
import com.neighbourly.app.c_business.usecase.profile.HouseholdManagementUseCase
import com.neighbourly.app.c_business.usecase.profile.NeighbourhoodManagementUseCase
import com.neighbourly.app.c_business.usecase.profile.ProfileImageUpdateUseCase
import com.neighbourly.app.c_business.usecase.profile.ProfileRefreshUseCase
import com.neighbourly.app.c_business.usecase.profile.ProfileUpdateUseCase
import com.neighbourly.app.d_entity.interf.Api
import com.neighbourly.app.d_entity.interf.Db
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
    }
val adapterModule =
    module {
        single<Api> {
            ApiGateway(KtorApi())
        }
        single<Db> {
            DbInteractor(createDatabase())
        }
        single {
            NavigationViewModel(get())
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
            ProfileMenuViewModel(get(), get())
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
            MapViewModel(get(), get(), get(), get())
        }
        factory {
            HouseholdLocalizeViewModel(get(), get())
        }
        factory {
            HouseholdInfoEditViewModel(get(), get())
        }
        factory {
            NeighbourhoodInfoViewModel(get(), get())
        }
        factory {
            HouseholdAddMemberViewModel(get(), get(), get())
        }
        factory {
            NeighbourhoodAddMemberViewModel(get(), get(), get())
        }
        factory {
            FilteredItemListViewModel(get(), get())
        }
    }

val useCaseModule =
    module {
        single {
            LoginUseCase(get(), get(), get())
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
            HouseholdLocalizeUseCase(get(), get())
        }
        single {
            HouseholdManagementUseCase(get(), get())
        }
        single {
            NeighbourhoodManagementUseCase(get(), get())
        }
        single {
            FetchProfileUseCase(get(), get())
        }
        single {
            ContentSyncUseCase(get(), get(), get())
        }
    }
