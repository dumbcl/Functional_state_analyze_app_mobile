package com.example.diplomapplication.di

import android.content.Context
import android.content.SharedPreferences
import com.example.diplomapplication.data.TestsRepository
import com.example.diplomapplication.data.TestsRepositoryImpl
import com.example.diplomapplication.data.network.ApiRepository
import com.example.diplomapplication.data.network.AuthInterceptor
import com.example.diplomapplication.ui.enter_screen.EnterViewModel
import com.example.diplomapplication.ui.escal_daily_screen.EscalDailyScreenViewModel
import com.example.diplomapplication.ui.escal_screen.EscalScreenViewModel
import com.example.diplomapplication.ui.gench_screen.GenchScreenViewModel
import com.example.diplomapplication.ui.main_screen.MainScreenViewModel
import com.example.diplomapplication.ui.ppg_screen.PPGScreenViewModel
import com.example.diplomapplication.ui.profile_screen.ProfileScreenViewModel
import com.example.diplomapplication.ui.reactions_screen.ReactionsScreenViewModel
import com.example.diplomapplication.ui.rufie_screen.RufieScreenViewModel
import com.example.diplomapplication.ui.shtange_screen.ShtangeScreenViewModel
import com.example.diplomapplication.ui.strup_screen.StrupScreenViewModel
import com.example.diplomapplication.ui.text_audition_screen.TextAuditionScreenViewModel
import com.example.diplomapplication.ui.trends_screen.TrendsScreenViewModel
import com.example.diplomapplication.util.API_ENDPOINT
import com.example.diplomapplication.util.CONNECT_TIMEOUT
import com.example.diplomapplication.util.READ_TIMEOUT
import com.example.diplomapplication.util.WRITE_TIMEOUT
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

fun initKoin(appDeclaration: KoinAppDeclaration = {}) =
    startKoin {
        appDeclaration()
        modules(
            repositoryModule,
            viewModelsModule,
        )
    }

val viewModelsModule = module {
    viewModel { MainScreenViewModel(get()) }
    viewModel { PPGScreenViewModel() }
    viewModel { ProfileScreenViewModel(get()) }
    viewModel { EscalScreenViewModel(get()) }
    viewModel { EscalDailyScreenViewModel(get()) }
    viewModel { EnterViewModel(get()) }
    viewModel { ShtangeScreenViewModel(get()) }
    viewModel { GenchScreenViewModel(get()) }
    viewModel { RufieScreenViewModel(get()) }
    viewModel { ReactionsScreenViewModel(get()) }
    viewModel { StrupScreenViewModel(get()) }
    viewModel { TextAuditionScreenViewModel(get()) }
    viewModel { TrendsScreenViewModel(get()) }
}

val repositoryModule = module {

    single { provideSharedPreferences(androidContext()) }
    single { provideAuthInterceptor(get()) }
    single { provideOkHttpClient(get()) }
    single { provideRetrofit(get()) }
    single { provideApiService(get()) }
    single<TestsRepository> {
        TestsRepositoryImpl(apiRepository = get(), sharedPreferences = get())
    }
}


private fun provideSharedPreferences(context: Context): SharedPreferences {
    return context.getSharedPreferences("APP_SHARED_PREFERENCES", Context.MODE_PRIVATE)
}

private fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
    return OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
        .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
        .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
        .build()
}

private fun provideAuthInterceptor(sharedPreferences: SharedPreferences): AuthInterceptor {
    return AuthInterceptor(sharedPreferences)
}

private fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
    return Retrofit.Builder()
        .baseUrl(API_ENDPOINT)
        .client(okHttpClient)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}

private fun provideApiService(retrofit: Retrofit): ApiRepository {
    return retrofit.create(ApiRepository::class.java)
}
