package com.katoh.campusschedule.models.prefs

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.katoh.campusschedule.utils.toColorList

class CustomSharedPreferences(
    val context: Context, private val preferenceNames: PreferenceNames
) {
    // does not extend SharedPreferences class
    // not to use the method
    private var sp: SharedPreferences =
        if (preferenceNames === PreferenceNames.DEFAULT) {
            PreferenceManager.getDefaultSharedPreferences(context)
        } else {
            context.getSharedPreferences(
                preferenceNames.name, Context.MODE_PRIVATE)
        }

    /**
     * Put a boolean value to preferences
     */
    fun putBoolean(key: PreferenceKeys, value: Boolean) {
        if (key.preferenceNames == preferenceNames && key.isBooleanKey()) {
            sp.edit().putBoolean(key.name, value).apply()
        } else {
            throw IllegalArgumentException()
        }
    }

    /**
     * Get a boolean value from preferences
     */
    fun getBoolean(key: PreferenceKeys): Boolean {
        if (key.preferenceNames == preferenceNames && key.isBooleanKey()) {
            return sp.getBoolean(key.name, key.defaultValue as Boolean)
        } else {
            throw IllegalArgumentException()
        }
    }

    /**
     * Put an integer value to preferences
     */
    fun putInt(key: PreferenceKeys, value: Int) {
        if (key.preferenceNames == preferenceNames && key.isIntKey()) {
            sp.edit().putInt(key.name, value).apply()
        } else {
            throw IllegalArgumentException()
        }
    }

    /**
     * Get an integer value from preferences
     */
    fun getInt(key: PreferenceKeys): Int {
        if (key.preferenceNames == preferenceNames && key.isIntKey()) {
            return sp.getInt(key.name, key.defaultValue as Int)
        } else {
            throw IllegalArgumentException()
        }
    }

    /**
     * Put a string value to preferences
     */
    fun putString(key: PreferenceKeys, value: String) {
        if (key.preferenceNames == preferenceNames && key.isStringKey()) {
            sp.edit().putString(key.name, value).apply()
        } else {
            throw IllegalArgumentException()
        }
    }

    /**
     * Get a string value from preferences
     */
    fun getString(key: PreferenceKeys): String {
        if (key.preferenceNames == preferenceNames && key.isIntKey()) {
            return sp.getString(key.name, key.defaultValue as String)
                ?: throw IllegalStateException()
        } else {
            throw IllegalArgumentException()
        }
    }

    /**
     * Put a list (any type) to preferences
     */
    fun putList(key: PreferenceKeys, values: List<*>) {
        if (key.preferenceNames == preferenceNames && key.isListKey()) {
            sp.edit().run {
                if (values.isNotEmpty()) {
                    putString(key.name, values.joinToString(","))
                } else {
                    putString(key.name, null)
                }
                apply()
            }
        } else {
            throw IllegalArgumentException()
        }
    }

    /**
     * Get a string list from preferences
     */
    fun getStringList(key: PreferenceKeys) : List<String> {
        if (key.preferenceNames == preferenceNames && key.isListKey()) {
            val defaultValue = getDefaultStringList(key.defaultValue as Int)
                .joinToString(",")
            return sp.getString(key.name, defaultValue)
                ?.split(",")
                ?: throw NullPointerException()
        } else {
            throw IllegalArgumentException()
        }
    }

    /**
     * Get a color integer list from preferences
     */
    fun getColorIntList(key: PreferenceKeys) : List<Int> {
        if (key.preferenceNames == preferenceNames && key.isListKey()) {
            val defaultValue = getDefaultColorList(key.defaultValue as Int)
                .joinToString(",")
            return sp.getString(key.name, defaultValue)
                ?.split(",")?.map { it.toInt() }
                ?: throw NullPointerException()
        } else {
            throw IllegalArgumentException()
        }
    }

    private fun getDefaultStringList(resId: Int) : List<String> {
        return context.resources.getStringArray(resId).toList()
    }

    private fun getDefaultColorList(resId: Int) : List<Int> {
        val resArray = context.resources.obtainTypedArray(resId)
        try {
            return resArray.toColorList()
        } finally {
            resArray.recycle()
        }
    }

}