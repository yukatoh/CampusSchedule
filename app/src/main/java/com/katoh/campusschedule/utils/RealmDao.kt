package com.katoh.campusschedule.utils

import com.katoh.campusschedule.models.dao.CourseDao
import com.katoh.campusschedule.models.dao.TermDao
import io.realm.Realm
import io.realm.RealmModel
import io.realm.RealmResults

fun Realm.termDao() : TermDao = TermDao(this)
fun Realm.courseDao() : CourseDao = CourseDao(this)
fun <T : RealmModel> RealmResults<T>.asLiveData() = RealmLiveData<T>(this)
