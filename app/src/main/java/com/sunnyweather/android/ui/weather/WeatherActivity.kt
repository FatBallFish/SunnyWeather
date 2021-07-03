package com.sunnyweather.android.ui.weather

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sunnyweather.android.BaseActivity
import com.sunnyweather.android.IndexViewPageAdapter
import com.sunnyweather.android.R
import com.sunnyweather.android.logic.dao.getConfig
import com.sunnyweather.android.logic.dao.setConfig
import com.sunnyweather.android.logic.model.Place
import com.sunnyweather.android.ui.FavViewModel
import com.sunnyweather.android.ui.place.PlaceFragment
import com.sunnyweather.android.ui.place.PlaceViewModel
import com.sunnyweather.android.ui.place.WeatherViewModel
import kotlinx.android.synthetic.main.activity_weather.*
import kotlinx.android.synthetic.main.forecast.*
import kotlinx.android.synthetic.main.life_index.*
import kotlinx.android.synthetic.main.now.*
import java.util.*

class WeatherActivity : BaseActivity(), NavigateListener {
    val placeViewModel by lazy { ViewModelProvider(this)[PlaceViewModel::class.java] }
    val weatherViewModel by lazy { ViewModelProvider(this)[WeatherViewModel::class.java] }
    val favViewModel by lazy { ViewModelProvider(this)[FavViewModel::class.java] }
    private var currentIndex = 0
    private var fav = false // 是否已收藏
    private val listenerMap = HashMap<Int, RefreshListener>(16)


    //    private var refreshListener: RefreshListener? = null;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 清除状态栏的样式
//        val decorView = window.decorView
//        decorView.systemUiVisibility =
//            View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//        window.statusBarColor = Color.TRANSPARENT
        // OVER
        setContentView(R.layout.activity_weather)
        initConfig()
        initUI()
        initViewModel()
    }

    private fun initConfig() {
        currentIndex = intent.getIntExtra("index", 0)
        fav = favViewModel.contain(
            favViewModel.getFavList(),
            placeViewModel.getSavedPlace(currentIndex)
        )
        Log.d(TAG, "currentIndex:$currentIndex")
    }

    private fun initUI() {
        favBtn.setBackgroundResource(if (fav) R.drawable.ic_fav else R.drawable.ic_fav_bor)
        // 侧滑栏
        navBtn.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        if (placeViewModel.isPlaceSaved(currentIndex)) {
            val savedPlace = placeViewModel.getSavedPlace(currentIndex)
            placeName.text = savedPlace.name
        }
        val placeView = PlaceFragment.newInstance(currentIndex)
        supportFragmentManager.beginTransaction().replace(R.id.placeView, placeView)
        drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerStateChanged(newState: Int) {}

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}

            override fun onDrawerClosed(drawerView: View) {
                val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                manager.hideSoftInputFromWindow(
                    drawerView.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS
                )
            }

            override fun onDrawerOpened(drawerView: View) {}
        })
        // viewPager2
        viewpager2.adapter = IndexViewPageAdapter(this)
        viewpager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currentIndex = position;
                val savedPlace = placeViewModel.getSavedPlace(currentIndex)
                placeName.text = savedPlace.name
                supportFragmentManager.beginTransaction()
                    .replace(R.id.placeView, PlaceFragment.newInstance(currentIndex))
                setConfig("index", currentIndex)
                if (!placeViewModel.isPlaceSaved(currentIndex)) {
                    showToast("当前选项卡暂无天气信息，请选择一个地点")
                    drawerLayout.openDrawer(GravityCompat.START)
                } else {
                    refreshWeather(currentIndex)
                }
            }
        })
        favBtn.setOnClickListener {
            val savedPlace = placeViewModel.getSavedPlace(currentIndex)
            if (fav) {
                favViewModel.deleteFav(place = savedPlace)
                favBtn.setBackgroundResource(R.drawable.ic_fav_bor)
                showToast("cancel fav")
                fav = false
            } else {
                favViewModel.addFav(place = savedPlace)
                favBtn.setBackgroundResource(R.drawable.ic_fav)
                showToast("add fav")
                fav = true
            }
        }
    }

    private fun initViewModel() {
        refreshWeather(currentIndex)
    }

    fun refreshWeather(index: Int) {
        listenerMap[index]?.refresh()
        fav = favViewModel.contain(
            favViewModel.getFavList(),
            placeViewModel.getSavedPlace(currentIndex)
        )
        favBtn.setBackgroundResource(if (fav) R.drawable.ic_fav else R.drawable.ic_fav_bor)
//        refreshListener?.refresh()
    }

    override fun onNavigate(to: Int, bundle: Bundle?) {
        TODO("Not yet implemented")
    }

    override fun setRefreshListener(index: Int, listener: RefreshListener) {
        listenerMap[index] = listener;
//        refreshListener = listener
    }
}