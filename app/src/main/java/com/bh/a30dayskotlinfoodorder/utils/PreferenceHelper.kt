package com.bh.a30dayskotlinfoodorder.utils

import android.content.Context
import android.content.SharedPreferences

object PreferenceHelper {

    val CART_MODEL:String = "CART_MODEL"
    val CUSTOMER_ID : String = "CUSTOMER_ID"
    val STATUS_ID : String = "STATUS_ID"

    private val prefApp = "AppPrefence"
    private val preferencesName = prefApp
    private var preferences: SharedPreferences

    init {
        preferences = MyApplication.instance.applicationContext.getSharedPreferences(preferencesName, Context.MODE_PRIVATE)
    }

    fun putValue(key: String?, value: Int) {
        preferences.edit().putInt(key, value).apply()
    }

    fun putValue(key: String?, value: String?) {
        preferences.edit().putString(key, value).apply()
    }

    fun putValue(key: String?, value: Boolean) {
        preferences.edit().putBoolean(key, value).apply()
    }

    fun getInt(key: String?, defaultValue: Int = 0): Int {
        return preferences.getInt(key, defaultValue)
    }

    fun getString(key: String, defaultValue: String = "") : String {
        return preferences.getString(key, defaultValue)!!
    }

    fun getBoolean(key: String?, defaultValue: Boolean = false): Boolean {
        return preferences.getBoolean(key, defaultValue)
    }

    fun clearAllData() {
        preferences.edit().clear().apply()
    }

}