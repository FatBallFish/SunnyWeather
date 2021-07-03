package com.sunnyweather.android.ui

import androidx.lifecycle.ViewModel
import com.sunnyweather.android.logic.Repository
import com.sunnyweather.android.logic.model.Place

/**
 * @description
 * @author FatBallFish
 * @Date 2021/7/3 14:33
 * */

class FavViewModel : ViewModel() {
    fun getFavList() = Repository.getFavList()
    fun addFav(place: Place) = Repository.addFav(place)
    fun deleteFav(place: Place) = Repository.deleteFav(place)
    fun contain(list: List<Place>, place: Place): Boolean {
        for (it in list) {
            if (it.location.lat == place.location.lat && it.location.lng == place.location.lng) {
                return true
            }
        }
        return false
    }
}