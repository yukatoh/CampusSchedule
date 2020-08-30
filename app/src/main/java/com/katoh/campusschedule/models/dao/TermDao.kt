package com.katoh.campusschedule.models.dao

import com.katoh.campusschedule.models.entity.CourseRealmObject
import com.katoh.campusschedule.models.entity.TermRealmObject
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import io.realm.kotlin.createObject
import io.realm.kotlin.where

class TermDao(val realm: Realm) {
    // About all terms
    val allTerms: RealmResults<TermRealmObject>
        get() = realm.where<TermRealmObject>().findAllAsync()
            .sort("id", Sort.DESCENDING)

    // About search
    /**
     * Find a term object equal to the selected id
     * @param selectedId The id of the selected term object
     */
    fun findTermById(selectedId: Long) : TermRealmObject {
        return realm.where<TermRealmObject>()
            .equalTo("id", selectedId)
            .findFirst() ?: throw Exception("selectedId is invalid")
    }

    /**
     * Get realm results of the courses of the selected term
     * except the name is empty
     */
    fun getCourseResults(selectedId: Long): RealmResults<CourseRealmObject> {
        return findTermById(selectedId).courses
            .where().isNotEmpty("courseName").findAll()
            .sort("order").sort("day")
    }

    // About Transaction
    /**
     * Add a term info to realm database
     * @param term the term info, usually from edit text forms
     */
    fun createTransaction(term: TermRealmObject) {
        realm.executeTransaction {
            realm.createObject<TermRealmObject>(maxId + 1L)
                .apply {
                    termLabel = term.termLabel
                    startYear = term.startYear
                    startMonth = term.startMonth
                    endYear = term.endYear
                    endMonth = term.endMonth
                    courses.addAll(initCourses)
                    courses.sort("order").sort("day")
            }
        }
    }

    /**
     * Initialized course list to create term object
     */
    private val initCourses: MutableList<CourseRealmObject>
        get() {
            val courses = mutableListOf<CourseRealmObject>()
            for (d in 0..5) {
                for (i in 1..7) {
                    val course = CourseRealmObject().apply {
                        day = d
                        order = i
                    }
                    courses.add(course)
                }
            }
            return courses
        }

    /**
     * Renew the term info
     * @param selectedId The id of the selected term object
     * @param term The renewal term object
     */
    fun setTransaction(selectedId: Long, term: TermRealmObject) {
        realm.executeTransaction {
            findTermById(selectedId).apply {
                termLabel = term.termLabel
                startYear = term.startYear
                startMonth = term.startMonth
                endYear = term.endYear
                endMonth = term.endMonth
            }
        }
    }

    /**
     * Delete a term from realm database
     * @param selectedId the id of the selected term object
     */
    fun deleteTransaction(selectedId: Long) {
        realm.executeTransaction {
            realm.where<TermRealmObject>()
                .equalTo("id", selectedId)
                .findFirst()
                ?.deleteFromRealm()
        }
    }

    /**
     * The maximum of ID at the time
     */
    val maxId: Long
        get() = realm.where<TermRealmObject>().max("id")?.toLong() ?: 0L

}
