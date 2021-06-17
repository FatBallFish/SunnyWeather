package com.sunnyweather.android.logic.dao

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.google.android.material.snackbar.Snackbar
import com.sunnyweather.android.BaseFragment
import com.sunnyweather.android.SunnyWeatherApplication
import java.lang.RuntimeException

/**
 * @description
 * @author FatBallFish
 * @Date 2021/2/4 20:39
 * */
/**
 * 获得SharedPreferences的配置。
 * String   类型默认值为 ""
 * Int      类型默认值为 0
 * Float    类型默认值为 0F
 * Long     类型默认值为 0L
 * Boolean  类型默认值为 false
 * 其他类型抛出 RuntimeException错误
 */
inline fun <reified T> Fragment.getConfig(
    key: String,
    default: T? = null,
    db: String = "default"
): T {
    val preference: SharedPreferences
    var _key: String = key
    Log.i(BaseFragment.TAG, "T的类型:${T::class.java.simpleName}")
    when (db) {
        "default" -> {
            preference =
                PreferenceManager.getDefaultSharedPreferences(SunnyWeatherApplication.context)
        }
        "flutter" -> {
            preference =
                SunnyWeatherApplication.context.getSharedPreferences(
                    "FlutterSharedPreferences",
                    Context.MODE_PRIVATE
                )
            _key = "flutter.$key"
        }
        else -> {
            preference =
                SunnyWeatherApplication.context.getSharedPreferences(db, Context.MODE_PRIVATE)
        }
    }
    when (T::class.java.simpleName) {
        "String" -> {
            return preference.getString(_key, if (default != null) default as String else "") as T
        }
        "Int", "Integer" -> {
            return preference.getInt(_key, if (default != null) default as Int else 0) as T
        }
        "Float" -> {
            return preference.getFloat(_key, if (default != null) default as Float else 0F) as T
        }
        "Long" -> {
            return preference.getLong(_key, if (default != null) default as Long else 0L) as T
        }
        "Boolean" -> {
            return preference.getBoolean(
                _key,
                if (default != null) default as Boolean else false
            ) as T
        }
        else -> {
            throw RuntimeException("invalid value type")
        }
    }
}

inline fun <reified T> Fragment.setConfig(key: String, value: T, db: String = "default") {
    val preference: SharedPreferences
    var _key: String = key
    when (db) {
        "default" -> {
            preference =
                PreferenceManager.getDefaultSharedPreferences(SunnyWeatherApplication.context)
        }
        "flutter" -> {
            preference =
                SunnyWeatherApplication.context.getSharedPreferences(
                    "FlutterSharedPreferences",
                    Context.MODE_PRIVATE
                )
            _key = "flutter.$key"
        }
        else -> {
            preference =
                SunnyWeatherApplication.context.getSharedPreferences(db, Context.MODE_PRIVATE)
        }
    }

    Log.i(BaseFragment.TAG, "T的类型:${T::class.java.simpleName}")
    when (T::class.java.simpleName) {
        "String" -> {
            preference.edit().putString(_key, value as String).apply()
        }
        "Int" -> {
            preference.edit().putInt(_key, value as Int).apply()
        }
        "Float" -> {
            preference.edit().putFloat(_key, value as Float).apply()
        }
        "Long" -> {
            preference.edit().putLong(_key, value as Long).apply()
        }
        "Boolean" -> {
            preference.edit().putBoolean(_key, value as Boolean).apply()
        }
        else -> {
            throw RuntimeException("invalid value type")
        }
    }
}

fun Fragment.removeConfig(key: String, db: String = "default") {
    val preference: SharedPreferences
    var _key: String = key
    when (db) {
        "default" -> {
            preference =
                PreferenceManager.getDefaultSharedPreferences(SunnyWeatherApplication.context)
        }
        "flutter" -> {
            preference =
                SunnyWeatherApplication.context.getSharedPreferences(
                    "FlutterSharedPreferences",
                    Context.MODE_PRIVATE
                )
            _key = "flutter.$key"
        }
        else -> {
            preference =
                SunnyWeatherApplication.context.getSharedPreferences(db, Context.MODE_PRIVATE)
        }
    }
    preference.edit().remove(_key).apply()
}

fun Fragment.showToast(msg: String, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(SunnyWeatherApplication.context, msg, length).show()
}

fun Fragment.showSnackBar(msg: String, view: View) {
    Snackbar.make(view, msg, Snackbar.LENGTH_SHORT).show()
}

fun Fragment.showSnackBar(msg: String, length: Int = Snackbar.LENGTH_SHORT, view: View) {
    Snackbar.make(view, msg, length).show()
}