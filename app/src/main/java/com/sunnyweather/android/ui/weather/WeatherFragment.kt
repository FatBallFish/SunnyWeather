package com.sunnyweather.android.ui.weather

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.sunnyweather.android.R
import com.sunnyweather.android.logic.dao.showToast
import com.sunnyweather.android.logic.model.Weather
import com.sunnyweather.android.logic.model.getSky
import com.sunnyweather.android.ui.place.PlaceViewModel
import com.sunnyweather.android.ui.place.WeatherViewModel
import kotlinx.android.synthetic.main.activity_weather.*
import kotlinx.android.synthetic.main.forecast.*
import kotlinx.android.synthetic.main.fragment_weather.*
import kotlinx.android.synthetic.main.life_index.*
import kotlinx.android.synthetic.main.now.*
import java.text.SimpleDateFormat
import java.util.*

class WeatherFragment : Fragment(), RefreshListener {
    private var name: String? = null
    private var index: Int = 0
    val weatherViewModel by lazy { ViewModelProvider(this)[WeatherViewModel::class.java] }
    val placeViewModel by lazy { ViewModelProvider(this)[PlaceViewModel::class.java] }
    private lateinit var mListener: NavigateListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mListener = context as NavigateListener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            index = it.getInt("index")
            name = it.getString("name")
        }
        mListener.setRefreshListener(index, this as RefreshListener)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_weather, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initConfig()
        initUI()
        initViewModel()
//        if (weatherViewModel.locationLng.isEmpty()) {
//
//            weatherViewModel.locationLng = intent.getStringExtra("location_lng") ?: ""
//        }
//        if (weatherViewModel.locationLat.isEmpty()) {
//            weatherViewModel.locationLat = intent.getStringExtra("location_lat") ?: ""
//        }
//        if (weatherViewModel.placeName.isEmpty()) {
//            weatherViewModel.placeName = intent.getStringExtra("place_name") ?: ""
//        }
    }

    private fun initConfig() {
        if (weatherViewModel.place == null && placeViewModel.isPlaceSaved(index)) {
            weatherViewModel.place = placeViewModel.getSavedPlace(index);
        } else {
            showToast("天气信息配置无效")
        }
    }

    private fun initUI() {
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary)
//        weatherViewModel.refreshWeather(
//            weatherViewModel.place!!.location.lng,
//            weatherViewModel.place!!.location.lat
//        )
        swipeRefresh.setOnRefreshListener {
            refreshWeather()
        }
    }

    private fun initViewModel() {

        weatherViewModel.weatherLiveData.observe(viewLifecycleOwner, Observer { result ->
            val weather = result.getOrNull()
            if (weather != null) {
                showWeatherInfo(weather)
            } else {
                showToast("无法成功获取天气信息", Toast.LENGTH_SHORT)
                result.exceptionOrNull()?.printStackTrace()
            }
            swipeRefresh.isRefreshing = false
        })
    }

    private fun refreshWeather() {
        weatherViewModel.place = placeViewModel.getSavedPlace(index)
        if (weatherViewModel.place == null) {
            showToast("位置信息为空")
            return
        }
        weatherViewModel.refreshWeather(
            weatherViewModel.place!!.location.lng,
            weatherViewModel.place!!.location.lat
        )
        swipeRefresh.isRefreshing = true
    }

    private fun showWeatherInfo(weather: Weather) {
//        placeName.text = weatherViewModel.place!!.name
        val realtime = weather.realtime
        val daily = weather.daily
        // 填充now.xml布局中的数据
        val currentTempText = "${realtime.temperature.toInt()} ℃"
        currentTemp.text = currentTempText
        currentSky.text = getSky(realtime.skycon).info
        val currentPM25Text = "空气指数 ${realtime.airQuality.aqi.chn.toInt()}"
        currentAQI.text = currentPM25Text
        nowLayout.setBackgroundResource(getSky(realtime.skycon).bg)
        // 填充forecast.xml布局中的数据
        forecastLayout.removeAllViews()
        val days = daily.skyCon.size
        for (i in 0 until days) {
            val skycon = daily.skyCon[i]
            val temperature = daily.temperature[i]
            val view =
                LayoutInflater.from(context).inflate(R.layout.forecast_item, forecastLayout, false)
            val dateInfo = view.findViewById<TextView>(R.id.dateInfo)
            val skyIcon = view.findViewById<ImageView>(R.id.skyIcon)
            val skyInfo = view.findViewById<TextView>(R.id.skyInfo)
            val temperatureInfo = view.findViewById<TextView>(R.id.temperatureInfo)
            // 定义时间格式化器
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            dateInfo.text = simpleDateFormat.format(skycon.date)
            val sky = getSky(skycon.value)
            skyIcon.setImageResource(sky.icon)
            skyInfo.text = sky.info
            val tempText = "${temperature.min.toInt()}~${temperature.max.toInt()} ℃"
            temperatureInfo.text = tempText
            forecastLayout.addView(view)
        }
        // 填充life_index.xml布局中的数据
        val lifeIndex = daily.lifeIndex
        coldRiskText.text = lifeIndex.coldRisk[0].desc
        dressingText.text = lifeIndex.dressing[0].desc
        ultravioletText.text = lifeIndex.ultraviolet[0].desc
        carWashingText.text = lifeIndex.carWashing[0].desc
        weatherLayout.visibility = View.VISIBLE
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment WeatherFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(index: Int, name: String) =
            WeatherFragment().apply {
                arguments = Bundle().apply {
                    putString("name", name)
                    putInt("index", index)
                }
            }
    }

    override fun refresh() {
        refreshWeather()
    }
}