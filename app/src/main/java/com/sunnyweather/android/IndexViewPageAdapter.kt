package com.sunnyweather.android

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.sunnyweather.android.ui.weather.IndexOutBoundFragment
import com.sunnyweather.android.ui.weather.WeatherFragment

/**
 * @description
 * @author FatBallFish
 * @Date 2021/2/1 23:53
 * */

class IndexViewPageAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {
    private val NUM_PAGE = 2
    override fun getItemCount(): Int = NUM_PAGE

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> {
                return WeatherFragment.newInstance(0, "")
            }
            1 -> {
                return WeatherFragment.newInstance(1, "")
            }
            else -> {
                return IndexOutBoundFragment.newInstance(null, null)
            }
        }
    }
}