package com.katoh.campusschedule.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.katoh.campusschedule.models.entity.CourseRealmObject
import io.realm.RealmResults
import io.realm.Sort

class SortViewModel : ViewModel() {
    /**
     * Index item to sort
     * @param isAscending whether the item's sort order is ascending
     */
    enum class SortItem(var isAscending: Boolean) {
        DAY_ORDER(true),
        COURSE_NAME(true),
        TYPE(true),
        POINT(true)
    }

    // Live data of a selected SortItem Object
    private val _focusedItemData: MutableLiveData<SortItem> =
        MutableLiveData<SortItem>().apply {
            value = SortItem.DAY_ORDER
        }
    val focusedItemData: LiveData<SortItem>
        get() = _focusedItemData
    private val focusedItem: SortItem
        get() = _focusedItemData.value
            ?: throw Exception("Cannot get focusedItemData")

    /**
     * Update a focused sort item
     * according to two below functions
     */
    fun updateSortItem(name: String) {
        if (focusedItem.name == name) {
            updateSortOrder()
        } else {
            updateFocusedItem(name)
        }
    }

    /**
     * Update a focused sort item
     * if a focused one equals to a current one
     */
    private fun updateSortOrder() {
        _focusedItemData.value = focusedItem.apply {
            isAscending = isAscending.not()
        }
    }

    /**
     * Update a focused sort item
     * if a focused one does not equals to a current one
     */
    private fun updateFocusedItem(name: String) {
        _focusedItemData.value = SortItem.valueOf(name).apply {
            isAscending = true
        }
    }

    // A realm results storing course objects
    lateinit var courseResults: RealmResults<CourseRealmObject>

    fun updateCourseResults(results: RealmResults<CourseRealmObject>) {
        courseResults = sorted(results)
    }

    /**
     * Sort course objects
     * @return sorted RealmResults object
     */
    private fun sorted(
        realmResults: RealmResults<CourseRealmObject>
    ): RealmResults<CourseRealmObject> {
        val order =
            if (focusedItem.isAscending) {
                Sort.ASCENDING
            } else {
                Sort.DESCENDING
            }

        return when (focusedItem) {
            SortItem.DAY_ORDER -> {
                realmResults.sort(arrayOf("day", "order"), arrayOf(order, order))
            }
            SortItem.COURSE_NAME -> {
                realmResults.sort("courseName", order)
            }
            SortItem.TYPE -> {
                realmResults.sort("type", order)
            }
            SortItem.POINT -> {
                realmResults.sort("point", order)
            }
        }
    }

}