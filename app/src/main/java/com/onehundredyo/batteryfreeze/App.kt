package com.onehundredyo.batteryfreeze

import android.app.Application
import android.content.Context

class App : Application() {
    companion object {
        lateinit var prefs: MySharedPreferences
    }

    override fun onCreate() {
        prefs = MySharedPreferences(applicationContext)
        super.onCreate()
    }
}

class MySharedPreferences(context: Context) {
    private val datePrefs = context.getSharedPreferences("savedDate", Context.MODE_PRIVATE)
    private val textPrefs = context.getSharedPreferences("savedText", Context.MODE_PRIVATE)
    fun getSavedDate(key: String, defValue: String): String {
        return datePrefs.getString(key, defValue).toString()
    }

    fun setSavedDate(key: String, value: String) {
        datePrefs.edit().putString(key, value).apply()
    }

    fun getSavedText(key: String, defValue: String): String {
        return textPrefs.getString(key, defValue).toString()
    }

    fun setSavedText(key: String, value: String) {
        textPrefs.edit().putString(key, value).apply()
    }
}