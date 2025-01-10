package com.neighbourly.app

import com.neighbourly.app.a_device.api.KtorApi
import com.neighbourly.app.a_device.remote.PahoMqttIot
import com.neighbourly.app.a_device.store.SessionHybridStore
import com.neighbourly.app.b_adapt.gateway.ApiGateway
import com.neighbourly.app.b_adapt.interactor.DbInteractor
import com.neighbourly.app.b_adapt.viewmodel.SignalViewModel
import com.neighbourly.app.b_adapt.viewmodel.WebMapViewModel
import com.neighbourly.app.b_adapt.viewmodel.auth.LoginViewModel
import com.neighbourly.app.b_adapt.viewmodel.auth.RegisterViewModel
import com.neighbourly.app.b_adapt.viewmodel.box.BoxManagementViewModel
import com.neighbourly.app.b_adapt.viewmodel.items.FilteredItemListViewModel
import com.neighbourly.app.b_adapt.viewmodel.items.ItemDetailsViewModel
import com.neighbourly.app.b_adapt.viewmodel.items.MainContentViewModel
import com.neighbourly.app.b_adapt.viewmodel.items.RemindersViewModel
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
import com.neighbourly.app.c_business.usecase.box.BoxOpsUseCase
import com.neighbourly.app.c_business.usecase.content.ContentSyncUseCase
import com.neighbourly.app.c_business.usecase.content.FilterItemsUseCase
import com.neighbourly.app.c_business.usecase.content.ItemManagementUseCase
import com.neighbourly.app.c_business.usecase.profile.FetchProfileUseCase
import com.neighbourly.app.c_business.usecase.profile.HouseholdLocalizeUseCase
import com.neighbourly.app.c_business.usecase.profile.HouseholdManagementUseCase
import com.neighbourly.app.c_business.usecase.profile.NeighbourhoodManagementUseCase
import com.neighbourly.app.c_business.usecase.profile.ProfileImageUpdateUseCase
import com.neighbourly.app.c_business.usecase.profile.ProfileRefreshUseCase
import com.neighbourly.app.c_business.usecase.profile.ProfileUpdateUseCase
import com.neighbourly.app.d_entity.interf.Api
import com.neighbourly.app.d_entity.interf.ConfigStatusSource
import com.neighbourly.app.d_entity.interf.Db
import com.neighbourly.app.d_entity.interf.Iot
import com.neighbourly.app.d_entity.interf.KeyValueRegistry
import com.neighbourly.app.d_entity.interf.SessionStore
import com.neighbourly.app.d_entity.interf.StatusUpdater
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
        single<ConfigStatusSource> {
            statusConfigSource
        }
        single<StatusUpdater> {
            statusConfigSource
        }
    }
val adapterModule =
    module {
        single<Api> {
            ApiGateway(KtorApi, get())
        }
        single<Iot> {
            PahoMqttIot()
        }
        single<Db> {
            DbInteractor(createDatabase())
        }
        single {
            NavigationViewModel(get(), get())
        }
        factory {
            LoginViewModel(get(), get())
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
            LoginViewModel(get(), get())
        }
        factory {
            WebMapViewModel(get(), get(), get(), get())
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
            FilteredItemListViewModel(get(), get(), get(), get())
        }
        factory {
            MainContentViewModel(get(), get())
        }
        factory {
            ItemDetailsViewModel(get(), get(), get(), get())
        }
        factory {
            SignalViewModel(get())
        }
        factory {
            BoxManagementViewModel(get(), get(), get(), get())
        }
        factory {
            RemindersViewModel(get())
        }
    }

val useCaseModule =
    module {
        factory {
            LoginUseCase(get(), get(), get())
        }
        factory {
            LogoutUseCase(get(), get(), get())
        }
        factory {
            RegisterUseCase(get(), get())
        }
        factory {
            ProfileImageUpdateUseCase(get(), get())
        }
        factory {
            ProfileRefreshUseCase(get(), get())
        }
        factory {
            ProfileUpdateUseCase(get(), get())
        }
        factory {
            HouseholdLocalizeUseCase(get(), get())
        }
        factory {
            HouseholdManagementUseCase(get(), get())
        }
        factory {
            NeighbourhoodManagementUseCase(get(), get())
        }
        factory {
            FetchProfileUseCase(get(), get())
        }
        factory {
            ContentSyncUseCase(get(), get(), get())
        }
        factory {
            ItemManagementUseCase(get(), get(), get())
        }
        factory {
            BoxOpsUseCase(get(), get())
        }
        factory {
            FilterItemsUseCase(get())
        }
    }
