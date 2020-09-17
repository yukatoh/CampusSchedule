package com.katoh.campusschedule.models.dao

import com.katoh.campusschedule.models.entity.CourseRealmObject
import com.katoh.campusschedule.utils.termDao
import io.realm.Realm

class CourseDao(val realm: Realm) {
    // About search
    /**
     * Find a term object equal to the selected day and order
     * @param id The id of the selected term object
     * @param day The day index of the selected course object
     * @param order The order of the selected course object
     */
    fun findCourseByDayOrder(id: Long, day: Int, order: Int) : CourseRealmObject {
        return realm.termDao().findTermById(id).courses.where()
            .equalTo("day", day)
            .equalTo("order", order)
            .findFirst() ?: throw Exception("day-order is invalid")
    }

    // About Transaction
    /**
     * Renew the course data
     * @param selectedId The id of the selected term object
     * @param day The day index of the selected course object
     * @param order The order of the selected course object
     * @param course The renewal course object
     */
    fun setTransaction(
        selectedId: Long, day: Int, order: Int, course: CourseRealmObject
    ) {
        realm.executeTransaction {
            findCourseByDayOrder(selectedId, day, order).apply {
                courseName = course.courseName
                teacherName = course.teacherName
                type = course.type
                point = course.point
                grade = course.grade
                textbook = course.textbook
                bookTitle = course.bookTitle
                bookAuthor = course.bookAuthor
                bookPublisher = course.bookPublisher
                email = course.email
                url = course.url
                additional = course.additional
            }
        }
    }

    /**
     * Initialize the course data for all entities
     * @param selectedId The id of the selected term object
     * @param day The day index of the selected course object
     * @param order The order of the selected course object
     */
    fun initTransaction(selectedId: Long, day: Int, order: Int) {
        realm.executeTransaction {
            findCourseByDayOrder(selectedId, day, order).apply {
                courseName = ""
                teacherName = ""
                type = -1
                point = 0
                grade = 0
                textbook = ""
                bookTitle = ""
                bookAuthor = ""
                bookPublisher = ""
                email = ""
                url = ""
                additional = ""
            }
        }
    }
}