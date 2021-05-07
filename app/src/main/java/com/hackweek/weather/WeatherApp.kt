package com.hackweek.weather

import android.app.Application
import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import io.ktor.util.*
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

class WeatherApp : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@WeatherApp)
            modules(appModule)
        }
    }
}

val appModule = module {
    single { httpClient() }
    single { WeatherRepo(get()) }

    viewModel { MainViewModel(get()) }
}

private fun httpClient() = HttpClient {
    install(JsonFeature) {
        serializer = KotlinxSerializer(kotlinx.serialization.json.Json {
            prettyPrint = true
            ignoreUnknownKeys = true
        })
    }
    install(Logging){
        logger = Logger.DEFAULT
        level = LogLevel.BODY
    }
}