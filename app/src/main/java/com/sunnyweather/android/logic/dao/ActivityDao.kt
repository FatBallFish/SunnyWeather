package com.sunnyweather.android.logic.dao

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.preference.PreferenceManager
import com.sunnyweather.android.BaseActivity
import java.lang.RuntimeException

/**
 * @description
 * @author FatBallFish
 * @Date 2021/2/2 14:25
 * */
object ActivityDao {
    const val REQUEST_LOGIN = 100
    const val REQUEST_VOICE_LOGIN = 101;
    const val REQUEST_REGISTER = 200;

}

/**
 * 获得SharedPreferences的配置。
 * String   类型默认值为 ""
 * Int      类型默认值为 0
 * Float    类型默认值为 0F
 * Long     类型默认值为 0L
 * Boolean  类型默认值为 false
 * 其他类型抛出 RuntimeException错误
 */
inline fun <reified T> Activity.getConfig(
    key: String,
    default: T? = null,
    db: String = "default"
): T {
    val preference: SharedPreferences
    var _key: String = key
    Log.i(BaseActivity.TAG, "T的类型:${T::class.java.simpleName}")
    when (db) {
        "default" -> {
            preference = PreferenceManager.getDefaultSharedPreferences(this)
        }
        "flutter" -> {
            preference =
                this.getSharedPreferences("FlutterSharedPreferences", Context.MODE_PRIVATE)
            _key = "flutter.$key"
        }
        else -> {
            preference = this.getSharedPreferences(db, Context.MODE_PRIVATE)
        }
    }
    when (T::class.java.simpleName) {
        "String" -> {
            return preference.getString(_key, if (default != null) default as String else "") as T
        }
        "Int" -> {
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

inline fun <reified T> Activity.setConfig(key: String, value: T, db: String = "default") {
    val preference: SharedPreferences
    var _key: String = key
    when (db) {
        "default" -> {
            preference = PreferenceManager.getDefaultSharedPreferences(this)
        }
        "flutter" -> {
            preference =
                this.getSharedPreferences("FlutterSharedPreferences", Context.MODE_PRIVATE)
            _key = "flutter.$key"
        }
        else -> {
            preference = this.getSharedPreferences(db, Context.MODE_PRIVATE)
        }
    }

    Log.i(BaseActivity.TAG, "T的类型:${T::class.java.simpleName}")
    when (T::class.java.simpleName) {
        "String" -> {
            preference.edit().putString(_key, value as String).apply()
        }
        "Int", "Integer" -> {
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

fun Activity.removeConfig(key: String, db: String = "default") {
    val preference: SharedPreferences
    var _key: String = key
    when (db) {
        "default" -> {
            preference = PreferenceManager.getDefaultSharedPreferences(this)
        }
        "flutter" -> {
            preference =
                this.getSharedPreferences("FlutterSharedPreferences", Context.MODE_PRIVATE)
            _key = "flutter.$key"
        }
        else -> {
            preference = this.getSharedPreferences(db, Context.MODE_PRIVATE)
        }
    }
    preference.edit().remove(_key).apply()
}