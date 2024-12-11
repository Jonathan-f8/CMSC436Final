package com.example.cmsc436groupproject

import android.content.Context
import android.content.SharedPreferences

class LocalPreferences(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)

    fun savePreferences(groupSize: Int, location: String?) {
        val editor = sharedPreferences.edit()
        editor.putInt("preferred_group_size", groupSize)
        editor.putString("preferred_location", location)
        editor.apply()
    }

    fun getPreferredGroupSize(): Int {
        return sharedPreferences.getInt("preferred_group_size", 3)
    }

    fun getPreferredLocation(): String {
        return sharedPreferences.getString("preferred_location", "").orEmpty()
    }
}
