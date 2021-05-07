package com.hackweek.weather

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel
import kotlin.math.roundToInt

@ExperimentalAnimationApi
class MainActivity : ComponentActivity() {

    private val model: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            WeatherTheme {
                ProvideWindowInsets {
                    Surface {
                        CityWeather(model)
                    }
                }
            }
        }
    }
}

class MainViewModel(private val weatherRepo: WeatherRepo) : ViewModel() {

    val weather = MutableLiveData<LocationWeather>()

    init {
        viewModelScope.launch {
            weather.value = weatherRepo.getWeather()
        }
    }
}

@ExperimentalAnimationApi
@Composable
fun CityWeather(viewModel: MainViewModel) {
    val weatherState by viewModel.weather.observeAsState()
    val scrollState = rememberScrollState()

    weatherState?.let { weatherForecast ->
        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .background(gradient())
                .statusBarsPadding()
        ) {
            StatusHeader(
                cityName = "Stockholm",
                currentTemp = weatherForecast.current.temp,
                maxTemp = weatherForecast.daily[0].temp.max,
                minTemp = weatherForecast.daily[0].temp.min,
                weatherName = weatherForecast.current.weather[0].main,
                feelsLike = weatherForecast.current.feels_like,
                weatherId = weatherForecast.current.weather[0].id
            )
            Divider()
            HourList(
                forecast = weatherForecast.hourly.take(12)
            )
            Divider()
            DailyForecast(weatherForecast.daily)
            StatusDetails(
                sunriseTimestamp = weatherForecast.current.sunrise,
                sunsetTimestamp = weatherForecast.current.sunset,
                precipitationChance = weatherForecast.daily[0].pop,
                humidity = weatherForecast.current.humidity,
                windSpeed = weatherForecast.current.wind_speed,
                pressure = weatherForecast.current.pressure
            )
        }
    }

    if (weatherState == null) {
        Meteor()
    }
}

@ExperimentalAnimationApi
@Composable
fun Meteor() {
    val infiniteTransition = rememberInfiniteTransition()
    val offsetX by infiniteTransition.animateValue(
        initialValue = 0.dp,
        targetValue = 10.dp,
        typeConverter = Dp.VectorConverter,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    val offsetY by infiniteTransition.animateValue(
        initialValue = 0.dp,
        targetValue = 20.dp,
        typeConverter = Dp.VectorConverter,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(meteorGradient()),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(id = R.drawable.meteor),
            contentDescription = null,
            modifier = Modifier
                .size(256.dp)
                .aspectRatio(1f)
                .offset(offsetX, offsetY)
        )
    }
}

@Preview
@Composable
fun PreviewStatusHeader() {
    WeatherTheme {
        Surface {
            StatusHeader(
                cityName = "Stockholm",
                currentTemp = 23.2f,
                maxTemp = 25.8f,
                minTemp = 13.2f,
                weatherName = "Sunny",
                feelsLike = 22.1f,
                weatherId = 700
            )

        }
    }
}

@Composable
fun StatusHeader(
    cityName: String,
    currentTemp: Float,
    maxTemp: Float,
    minTemp: Float,
    weatherName: String,
    feelsLike: Float,
    weatherId: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp)
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = cityName,
                style = MaterialTheme.typography.h5
            )
            Spacer(
                modifier = Modifier.height(32.dp)
            )
            Text(
                text = "${currentTemp.roundToInt()}°",
                style = MaterialTheme.typography.h1,
                fontWeight = FontWeight.SemiBold,
            )
            Row {
                Row {
                    Icon(
                        imageVector = Icons.Filled.ExpandLess,
                        contentDescription = "Max temperature",
                        tint = Color(0x60ffffff)
                    )
                    Text(
                        text = "${maxTemp.roundToInt()}°",
                        style = MaterialTheme.typography.body1
                    )
                }
                Spacer(
                    modifier = Modifier.width(16.dp)
                )
                Row {
                    Icon(
                        imageVector = Icons.Filled.ExpandMore,
                        contentDescription = "Min temperature",
                        tint = Color(0x60ffffff)
                    )
                    Text(
                        text = "${minTemp.roundToInt()}°",
                        style = MaterialTheme.typography.body1
                    )
                }
            }
            Spacer(
                modifier = Modifier.height(32.dp)
            )
            Text(
                text = weatherName,
                style = MaterialTheme.typography.h5,
                fontWeight = FontWeight.Bold,
            )
            Spacer(
                modifier = Modifier.height(4.dp)
            )
            Text(
                text = "Feels like ${feelsLike.roundToInt()}°",
                style = MaterialTheme.typography.body1,
                color = Color(0x90ffffff)
            )
            Spacer(
                modifier = Modifier.height(16.dp)
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .offset(56.dp, 0.dp)
        ) {
            Spacer(
                modifier = Modifier.height(32.dp)
            )
            Icon(
                painter = painterResource(id = getIconFor(weatherId)),
                contentDescription = null,
                modifier = Modifier
                    .aspectRatio(1f)
            )
        }
    }
}

@Composable
fun Divider() {
    Box(
        modifier = Modifier
            .height(1.dp)
            .fillMaxWidth()
            .background(color = Color(0x60FFFFFF))
            .alpha(0.2f)
    )
}

@Composable
fun HourList(forecast: List<Hourly>) {
    LazyRow(
        contentPadding = PaddingValues(32.dp, 16.dp, 32.dp, 16.dp),
        horizontalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        itemsIndexed(
            items = forecast
        ) { index, item ->
            val time = if (index == 0) {
                "Now"
            } else {
                item.dt.hourOfDay()
            }
            HourlyTile(
                time = time,
                temp = item.temp.roundToInt()
            )
        }
    }
}

@Preview(
    backgroundColor = 0xFFFFC107,
    showBackground = true
)
@Composable
fun PreviewHourlyTile() {
    WeatherTheme {
        Surface {
            HourlyTile(
                time = "1 pm",
                temp = 23.12f.roundToInt()
            )
        }
    }
}

@Composable
fun HourlyTile(
    time: String,
    temp: Int
) {
    Column(
        modifier = Modifier.width(50.dp)
    ) {
        Text(
            text = time,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.body2,
            color = Color(0x90ffffff)
        )
        Spacer(
            modifier = Modifier.height(12.dp)
        )
        Text(
            text = "$temp°",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.h5,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
fun DailyForecast(forecast: List<Daily>) {
    Column(
        modifier = Modifier.padding(32.dp),
    ) {
        forecast.forEachIndexed { index, it ->
            DailyRow(
                dayName = it.dt.dayName(),
                weatherId = it.weather.getOrNull(0)?.id,
                precipitationChance = (it.pop * 100).roundToInt(),
                tempMax = it.temp.max.roundToInt(),
                tempMin = it.temp.min.roundToInt()
            )
            if (index < forecast.size - 1) {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFF5722)
@Composable
fun PreviewDailyList() {
    val list = listOf(
        Daily(
            dt = 1620377952,
            DayTemperature(
                min = 23.2f,
                max = 32.1f
            ),
            pop = 0.34f,
            weather = listOf(Weather(300, "Sunny"))
        ),
        Daily(
            dt = 1620377952,
            DayTemperature(
                min = -23.2f,
                max = -12.1f
            ),
            pop = 0.34f,
            weather = listOf(Weather(300, "Sunny"))
        ),
        Daily(
            dt = 1620377952,
            DayTemperature(
                min = 3.2f,
                max = 6.1f
            ),
            pop = 0.34f,
            weather = listOf(Weather(300, "Sunny"))
        ),
        Daily(
            dt = 1620377952,
            DayTemperature(
                min = 23.2f,
                max = 32.1f
            ),
            pop = 0.34f,
            weather = listOf(Weather(300, "Sunny"))
        ),
    )
    WeatherTheme {
        Surface {
            DailyForecast(list)
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFC107)
@Composable
fun PreviewDaily() {
    WeatherTheme {
        Surface {
            DailyRow(
                dayName = "Monday",
                weatherId = 600,
                precipitationChance = 32,
                tempMax = 23,
                tempMin = -17
            )
        }
    }
}

@Composable
fun DailyRow(
    dayName: String,
    weatherId: Int?,
    precipitationChance: Int,
    tempMax: Int,
    tempMin: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = dayName,
            modifier = Modifier
                .weight(0.5f),
            style = MaterialTheme.typography.body1,
            fontWeight = FontWeight.Bold,
        )
        Box(
            modifier = Modifier
                .weight(0.3f)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                weatherId?.let {
                    Box(
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = getIconFor(it)),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            tint = Color(0xaaffffff)
                        )
                    }
                }
                if (precipitationChance > 0) {
                    Spacer(
                        modifier = Modifier.width(16.dp)
                    )
                    Text(
                        text = "$precipitationChance%",
                        style = MaterialTheme.typography.body2,
                        color = Color(0x90ffffff)
                    )
                }
            }
        }
        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.3f)
        ) {
            Text(
                text = "$tempMax",
                modifier = Modifier.width(36.dp),
                textAlign = TextAlign.Start,
                fontWeight = FontWeight.Bold
            )
            Spacer(
                modifier = Modifier.width(4.dp)
            )
            Text(
                text = "$tempMin",
                modifier = Modifier.width(36.dp),
                textAlign = TextAlign.Start,
                fontWeight = FontWeight.Bold,
                color = Color(0x90ffffff)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFC107)
@Composable
fun PreviewStatus() {
    WeatherTheme {
        Surface {
            StatusTile(
                title = "Sunrise",
                value = "3:55 am"
            )
        }
    }
}

@Composable
fun StatusTile(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.body2,
            color = Color(0x90ffffff)
        )
        Spacer(
            modifier = Modifier.height(12.dp)
        )
        Text(
            text = value,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.h6,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
fun StatusDetails(
    sunriseTimestamp: Long,
    sunsetTimestamp: Long,
    precipitationChance: Float,
    humidity: Float,
    windSpeed: Float,
    pressure: Float
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0x18ffffff))
            .padding(32.dp)
            .navigationBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            StatusTile(
                title = "Sunrise",
                value = sunriseTimestamp.time(),
                modifier = Modifier.weight(1f)
            )
            StatusTile(
                title = "Sunset",
                value = sunsetTimestamp.time(),
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(32.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            StatusTile(
                title = "Preciptation",
                value = "${(precipitationChance * 100).roundToInt()}%",
                modifier = Modifier.weight(1f)
            )
            StatusTile(
                title = "Humidity",
                value = "${humidity.roundToInt()}%",
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(32.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            StatusTile(
                title = "Wind",
                value = "${windSpeed.roundToInt()} km/h",
                modifier = Modifier.weight(1f)
            )
            StatusTile(
                title = "Pressure",
                value = "${pressure.roundToInt()} hPa",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@ExperimentalAnimationApi
@Preview
@Composable
fun MeteorPreview() {
    WeatherTheme {
        Surface {
            Meteor()
        }
    }
}

val Nunito = FontFamily(
    Font(R.font.nunito_regular),
    Font(R.font.nunito_bold_regular, FontWeight.Bold),
)

@Composable
fun WeatherTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        content = content,
        colors = Colors(
            primary = Color.DarkGray,
            primaryVariant = Color.Gray,
            secondary = Color.DarkGray,
            secondaryVariant = Color.DarkGray,
            background = Color.Black,
            surface = Color.DarkGray,
            error = Color.Red,
            onPrimary = Color.White,
            onSecondary = Color.White,
            onBackground = Color.White,
            onSurface = Color.White,
            onError = Color.White,
            isLight = true
        ),
        typography = Typography(
            defaultFontFamily = Nunito,
            body2 = MaterialTheme.typography.body2.copy(color = Color(0xaaffffff))
        )
    )
}
