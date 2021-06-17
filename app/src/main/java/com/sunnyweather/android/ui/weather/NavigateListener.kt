package com.sunnyweather.android.ui.weather

import android.os.Bundle

/**
 * @description ViewPager Fragment与Activity之间通讯进行页面跳转的接口
 * @author FatBallFish
 * @Date 2021/2/3 23:33
 * */

interface NavigateListener {
    fun onNavigate(to: Int, bundle: Bundle?)
    fun setRefreshListener(index: Int, listener: RefreshListener)
}