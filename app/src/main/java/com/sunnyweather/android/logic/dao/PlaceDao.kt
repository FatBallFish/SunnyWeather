package com.sunnyweather.android.logic.dao

import android.content.Context
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sunnyweather.android.SunnyWeatherApplication
import com.sunnyweather.android.logic.model.Place
import java.lang.Exception

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

    fun getFavList(): List<Place> {
        val favJson = sharedPreferences().getString("favList", "[]") ?: "[]"
        val type = object : TypeToken<List<Place>>() {}.type
        return Gson().fromJson(favJson, type)
    }

    fun addFav(place: Place): Boolean {
        return try {
            val list = ArrayList<Place>()
            list.addAll(getFavList())
            for (it in list) {
                if (it.location.lat == place.location.lat && it.location.lng == place.location.lng) {
                    return true
                }
            }
            list.add(place)
            sharedPreferences().edit {
                putString("favList", Gson().toJson(list))
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun deleteFav(place: Place): Boolean {
        return try {
            val list = ArrayList<Place>()
            list.addAll(getFavList())
            for (it in list) {
                if (it.location.lat == place.location.lat && it.location.lng == place.location.lng) {
                    list.remove(it)
                    break
                }
            }
            sharedPreferences().edit {
                putString("favList", Gson().toJson(list))
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun sharedPreferences() =
        SunnyWeatherApplication.context.getSharedPreferences("sunny_weather", Context.MODE_PRIVATE)
}