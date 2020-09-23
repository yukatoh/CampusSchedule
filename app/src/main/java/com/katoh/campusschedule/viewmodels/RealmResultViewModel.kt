package com.katoh.campusschedule.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.katoh.campusschedule.models.entity.BookContent
import com.katoh.campusschedule.models.entity.CourseRealmObject
import com.katoh.campusschedule.models.entity.TermRealmObject
import com.katoh.campusschedule.utils.*
import io.realm.Realm
import io.realm.RealmResults

open class RealmResultViewModel : ViewModel() {
    val realm: Realm by lazy {
        Realm.getDefaultInstance()
    }

    val termResults: RealmResults<TermRealmObject>
        get() = realm.termDao().allTerms

    /**
     * Get realm results of the courses of the selected term
     * except the name is empty
     * usually for TimeRecyclerViewAdapter
     */
    val courseResults: RealmResults<CourseRealmObject>
        get() = realm.termDao().getCourseResults(selectedTerm.id)

    // Live data of a selected term
    private val _selectedTermData: MutableLiveData<TermRealmObject?> by lazy {
        MutableLiveData<TermRealmObject?>()
    }
    val selectedTermData: LiveData<TermRealmObject?>
        get() = _selectedTermData
    lateinit var selectedTerm: TermRealmObject

    /**
     * Change the selected term
     */
    fun chooseSelectedTerm(id: Long) {
        selectedTerm = realm.termDao().findTermById(id)
    }

    /**
     * Update live data of the term when changing the selected one
     */
    private fun updateTermData() {
        try {
            selectedTerm = realm.termDao().findTermById(selectedTerm.id)
            _selectedTermData.value = selectedTerm
        } catch (e: IllegalStateException) {
            // When all term data deleted
            _selectedTermData.value = null
        }

    }

    /**
     * Update realm object of the selected term with a given realm object
     */
    fun updateTermSetting(term: TermRealmObject) {
        realm.termDao().setTransaction(selectedTerm.id, term)
        updateTermData()
    }

    /**
     * Create realm object of a given term
     */
    fun createTerm(term: TermRealmObject) {
        realm.termDao().createTransaction(term)
        chooseSelectedTerm(maxId)       // Created term id is the maximum one
        updateTermData()
    }

    /**
     * Delete realm object of the selected term from the realm
     */
    fun deleteSelectedTerm() {
        realm.termDao().deleteTransaction(selectedTerm.id)
        updateTermData()
    }

    /**
     * The maximum of ID at the time
     */
    private val maxId: Long
        get() = realm.termDao().maxId

    // Live data of a selected course
    private val _selectedCourseData: MutableLiveData<CourseRealmObject> by lazy {
        MutableLiveData<CourseRealmObject>()
    }
    val selectedCourseData: LiveData<CourseRealmObject>
        get() = _selectedCourseData
    lateinit var selectedCourse: CourseRealmObject

    /**
     * Change the selected course
     */
    fun chooseSelectedCourse(day: Int, order: Int) {
        selectedCourse = realm.courseDao().findCourseByDayOrder(
            selectedTerm.id, day, order)
    }

    /**
     * Update live data of the course
     */
    private fun updateCourseData() {
        selectedCourse = realm.courseDao().findCourseByDayOrder(
            selectedTerm.id, selectedCourse.day, selectedCourse.order)
        _selectedCourseData.value = selectedCourse
    }

    /**
     * Update realm object of the selected course with a given realm object
     */
    fun updateCourseSetting(course: CourseRealmObject) {
        realm.courseDao().setTransaction(selectedTerm.id,
            selectedCourse.day, selectedCourse.order, course)
        updateCourseData()
    }

    /**
     * Update realm object of book contents of the selected course
     * with a given BookContent object
     */
    fun updateBookSetting(book: BookContent) {
        realm.courseDao().setBookTransaction(selectedTerm.id,
            selectedCourse.day, selectedCourse.order, book)
        updateCourseData()
    }

    /**
     * Initialize realm object of the selected course
     */
    fun initCourse() {
        realm.courseDao().initTransaction(
            selectedTerm.id, selectedCourse.day, selectedCourse.order)
        updateCourseData()
    }

    override fun onCleared() {
        super.onCleared()
        realm.close()
    }

    // Live data of temp book data
    private val _tempBookData: MutableLiveData<BookContent> by lazy {
        MutableLiveData<BookContent>()
    }
    val tempBookData: LiveData<BookContent>
        get() = _tempBookData
    lateinit var tempBook: BookContent

    private fun updateTempBookData() {
        _tempBookData.value = tempBook
    }

    fun updateTempBook(book: BookContent) {
        tempBook = book
        updateTempBookData()
    }

    fun initBook() {
        val book = BookContent(
            title = selectedCourse.bookTitle,
            author = selectedCourse.bookAuthor,
            publisher = selectedCourse.bookPublisher
        )
        updateTempBook(book)
    }

}