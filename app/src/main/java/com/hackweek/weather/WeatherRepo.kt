package com.hackweek.weather

import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.serialization.Serializable

class WeatherRepo(
    private val httpClient: HttpClient
) {
    private companion object {
        const val API_KEY = "1798df47d7479aeb448fb24cc442065"
    }

    suspend fun getWeather(): LocationWeather {
        val lat = 59.3467183
        val lon = 17.9922661
        val response = httpClient.get<LocationWeather>(
            "https://api.openweathermap.org/data/2.5/onecall?lat=$lat&lon=$lon&appid=$API_KEY&units=metric"
        )
        return response
    }
}

@Serializable
data class LocationWeather(
    val current: Status,
    val hourly: List<Hourly>,
    val daily: List<Daily>,
)

@Serializable
data class Status(
    val dt: Long,
    val sunrise: Long,
    val sunset: Long,
    val temp: Float,
    val feels_like: Float,
    val pressure: Float,
    val humidity: Float,
    val wind_speed: Float,
    val wind_deg: Int,
    val weather: List<Weather>,
)

@Serializable
data class Hourly(
    val dt: Long,
    val weather: List<Weather>,
    val temp: Float
)

@Serializable
data class Daily(
    val dt: Long,
    val temp: DayTemperature,
    val weather: List<Weather>,
    val pop: Float,
)

@Serializable
data class DayTemperature(
    val min: Float,
    val max: Float,
)

@Serializable
data class Weather(
    val id: Int,
    val main: String
)
