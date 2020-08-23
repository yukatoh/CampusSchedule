package com.katoh.campusschedule.models.dao

import com.katoh.campusschedule.models.entity.CourseRealmObject
import com.katoh.campusschedule.models.entity.TypeContent
import com.katoh.campusschedule.models.prefs.CustomSharedPreferences
import com.katoh.campusschedule.models.prefs.PreferenceKeys

class SettingDao(private val sp: CustomSharedPreferences) {
    data class SavedItem(
        val typeContents: List<TypeContent>,
        val satVisible: Boolean,
        val timeOrderMax: Int
    ) {
        /**
         * Get a type content (label & color) according to the selected course type
         */
        fun getUtilTypeContent(
            course: CourseRealmObject, defTypeContent: TypeContent
        ): TypeContent {
            return try {
                typeContents[course.type]
            } catch (e: ArrayIndexOutOfBoundsException) {
                // When the selected course type is initialized(-1)
                defTypeContent
            }
        }
    }

    /**
     * Get the type contents, saved values of shared preferences
     * or default ones of array resources
     */
    var savedTypeContents: List<TypeContent>
        get() = zip(savedLabels, savedColors)
        set(value) {
            val labels = mutableListOf<String>()
            val colors = mutableListOf<Int>()
            value.forEach { type: TypeContent ->
                labels.add(type.label)
                colors.add(type.color)
            }
            savedLabels = labels
            savedColors = colors
        }

    private var savedLabels: List<String>
        get() = sp.getStringList(PreferenceKeys.TYPE_LABELS)
        set(value) {
            sp.putList(PreferenceKeys.TYPE_LABELS, value)
        }

    private var savedColors: List<Int>
        get() = sp.getColorIntList(PreferenceKeys.TYPE_COLORS)
        set(value) {
            sp.putList(PreferenceKeys.TYPE_COLORS, value)
        }

    /**
     * A boolean value whether Saturday column of time table is visible
     */
    var satVisible: Boolean
        get() = sp.getBoolean(PreferenceKeys.SAT_VISIBLE)
        set(value) {
            sp.putBoolean(PreferenceKeys.SAT_VISIBLE, value)
        }

    var timeOrderMax: Int
        get() = sp.getInt(PreferenceKeys.TIME_ORDER_MAX)
        set(value) {
            sp.putInt(PreferenceKeys.TIME_ORDER_MAX, value)
        }


    companion object {
        private fun zip(labels: List<String>, colors: List<Int>): List<TypeContent> =
            labels.zip(colors) { a, b -> TypeContent(a, b) }
    }

}