package com.katoh.campusschedule.models.prefs

import com.katoh.campusschedule.R
import com.katoh.campusschedule.models.entity.BookContent

enum class PreferenceKeys(val preferenceNames: PreferenceNames,
                          val type: PreferenceType,
                          val defaultValue: Any) {
    TYPE_LABELS(PreferenceNames.DEFAULT, PreferenceType.LIST, R.array.labels_default_types),
    TYPE_COLORS(PreferenceNames.DEFAULT, PreferenceType.LIST, R.array.colors_default_types),
    SAT_VISIBLE(PreferenceNames.DEFAULT, PreferenceType.BOOLEAN, true),
    TIME_ORDER_MAX(PreferenceNames.DEFAULT, PreferenceType.INT, 7);

    enum class PreferenceType {
        BOOLEAN,
        INT,
        LONG,
        FLOAT,
        STRING,
        LIST
    }

    internal fun isBooleanKey(): Boolean = (type === PreferenceType.BOOLEAN)
    internal fun isIntKey(): Boolean = (type === PreferenceType.INT)
    internal fun isLongKey(): Boolean = (type === PreferenceType.LONG)
    internal fun isFloatKey(): Boolean = (type === PreferenceType.FLOAT)
    internal fun isStringKey(): Boolean = (type === PreferenceType.STRING)
    internal fun isListKey(): Boolean = (type === PreferenceType.LIST)

}