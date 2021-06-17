package com.sunnyweather.android.ui.place

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.sunnyweather.android.logic.Repository
import com.sunnyweather.android.logic.model.Location
import com.sunnyweather.android.logic.model.Place
import com.sunnyweather.android.logic.network.SunnyWeatherNetwork

class WeatherViewModel : ViewModel() {
    private val locationLiveData = MutableLiveData<Location>()
    var place: Place? = null
    var locationLng = ""
    var locationLat = ""
    var placeName = ""
    val weatherLiveData = Transformations.switchMap(locationLiveData) { input ->
        Repository.refreshWeather(input.lng, input.lat)
    }

    fun refreshWeather(lng: String, lat: String) {
        locationLng = lng
        locationLat = lat
        locationLiveData.value = Location(lng, lat)
    }
}