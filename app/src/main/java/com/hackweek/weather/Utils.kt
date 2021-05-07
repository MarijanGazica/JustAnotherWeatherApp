package com.hackweek.weather

import android.text.format.DateFormat
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import java.util.*

fun Long.dayName(): String {
    val calendar = Calendar.getInstance(Locale.ENGLISH)
    calendar.timeInMillis = this * 1000
    return calendar.get(Calendar.DAY_OF_WEEK).toDayName()
}

fun Long.time(): String {
    val calendar = Calendar.getInstance(Locale.ENGLISH)
    calendar.timeInMillis = this * 1000
    return DateFormat.format("hh:mm a", calendar)
        .toString()
        .toLowerCase(Locale.getDefault())
}

fun Int.toDayName() = when (this) {
    1 -> "Sunday"
    2 -> "Monday"
    3 -> "Tuesday"
    4 -> "Wednesday"
    5 -> "Thursday"
    6 -> "Friday"
    7 -> "Saturday"
    else -> "Unknown"
}

fun getIconFor(id: Int): Int = when (id) {
    in 200..299 -> R.drawable.thunderstorm
    in 300..399 -> R.drawable.rainy
    in 499..599 -> R.drawable.rain
    in 600..699 -> R.drawable.snowy
    in 700..799 -> R.drawable.haze
    800 -> R.drawable.sun
    801, 802 -> R.drawable.cloudy
    803, 804 -> R.drawable.cloud
    else -> R.drawable.meteor
}

fun gradient(): Brush {
    return Brush.verticalGradient(
        colors = listOf(
            Color(0xFFFF9800),
            Color(0xFFFF5722)
        )
    )
}

fun meteorGradient(): Brush {
    return Brush.verticalGradient(
        colors = listOf(
            Color(0xFFE91E63),
            Color(0xFFF44336)
        )
    )
}

fun Long.hourOfDay(): String {
    val calendar = Calendar.getInstance(Locale.ENGLISH)
    calendar.timeInMillis = this * 1000

    return DateFormat.format("h a", calendar)
        .toString()
        .toLowerCase(Locale.getDefault())
}
