package com.katoh.campusschedule.models.entity

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class TermRealmObject : RealmObject() {
    @PrimaryKey
    var id: Long = 0L
    // Term setting
    var termLabel: String = ""
    var startYear: String = ""
    var startMonth: String = ""
    var endYear: String = ""
    var endMonth: String = ""
    // Course setting
    var courses: RealmList<CourseRealmObject> = RealmList()
}

open class CourseRealmObject : RealmObject() {
    var courseName: String = ""
    var teacherName: String = ""
    var day: Int = 0
    var order: Int = 0
    var point: Long = 0L
    var grade: Long = 0L
    var type: Int = -1
    var email: String = ""
    var url: String = ""
    var textbook: String = ""
    var bookTitle: String = ""
    var bookAuthor: String = ""
    var bookPublisher: String = ""
    var additional: String = ""
}
