package com.katoh.campusschedule.utils

import com.katoh.campusschedule.models.dao.SettingDao
import com.katoh.campusschedule.models.prefs.CustomSharedPreferences

fun CustomSharedPreferences.settingDao() = SettingDao(this)