package com.sunnyweather.android.logic.dao

import android.content.Context
import androidx.core.content.edit
import com.google.gson.Gson
import com.sunnyweather.android.SunnyWeatherApplication
import com.sunnyweather.android.logic.model.Place

object PlaceDao {
    fun savePlace(place: Place, index: Int) {
        sharedPreferences().edit {
            putString("place$index", Gson().toJson(place))
        }
    }

    fun getSavedPlace(index: Int): Place {
        val placeJson = sharedPreferences().getString("place$index", "{}")
        return Gson().fromJson(placeJson, Place::class.java)
    }

    fun isPlaceSaved(index: Int) = sharedPreferences().contains("place$index")
    private fun sharedPreferences() =
        SunnyWeatherApplication.context.getSharedPreferences("sunny_weather", Context.MODE_PRIVATE)
}