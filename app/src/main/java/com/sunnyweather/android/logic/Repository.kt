package com.sunnyweather.android.logic


import androidx.lifecycle.liveData
import com.sunnyweather.android.logic.dao.PlaceDao
import com.sunnyweather.android.logic.model.DailyResponse
import com.sunnyweather.android.logic.network.SunnyWeatherNetwork
import kotlinx.coroutines.Dispatchers
import java.lang.Exception
import java.lang.RuntimeException
import com.sunnyweather.android.logic.model.Place
import com.sunnyweather.android.logic.model.Weather
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlin.coroutines.CoroutineContext

object Repository {
    fun searchPlaces(query: String) = fire(Dispatchers.IO) {
        val placeResponse = SunnyWeatherNetwork.searchPlaces(query)
        if (placeResponse.status == "ok") {
            val place = placeResponse.places
            Result.success(place)
        } else {
            Result.failure(RuntimeException("response status is ${placeResponse.status}"))
        }
    }

    fun getDailyWeather(lng: String, lat: String) = fire(Dispatchers.IO) {

        val dailyResponse = SunnyWeatherNetwork.getDailyWeather(lng, lat)
        if (dailyResponse.status == "ok") {
            val daily = dailyResponse.result.daily
            Result.success(daily)
        } else
            Result.failure(RuntimeException("response status is ${dailyResponse.status}"))

    }

    fun refreshWeather(lng: String, lat: String) = fire(Dispatchers.IO) {
        coroutineScope {
            val defferedRealtime = async {
                SunnyWeatherNetwork.getRealtimeWeather(lng, lat)
            }
            val defferedDaily = async {
                SunnyWeatherNetwork.getDailyWeather(lng, lat)
            }
            val realtimeResponse = defferedRealtime.await()
            val dailyResponse = defferedDaily.await()
            if (realtimeResponse.status == "ok" && dailyResponse.status == "ok") {
                val weather =
                    Weather(realtimeResponse.result.realTime, dailyResponse.result.daily)
                Result.success(weather)
            } else {
                Result.failure(RuntimeException("realtime response status is ${realtimeResponse.status}" + "daily response status is ${dailyResponse.status}"))
            }
        }
    }

    fun savePlace(place: Place,index:Int) = PlaceDao.savePlace(place,index)
    fun getSavedPlace(index:Int) = PlaceDao.getSavedPlace(index)
    fun isPlaceSaved(index:Int) = PlaceDao.isPlaceSaved(index)
    private fun <T> fire(context: CoroutineContext, block: suspend () -> Result<T>) =
        liveData<Result<T>>(context) {
            val result = try {
                block()
            } catch (e: Exception) {
                Result.failure<T>(e)
            }
            emit(result)
        }
}