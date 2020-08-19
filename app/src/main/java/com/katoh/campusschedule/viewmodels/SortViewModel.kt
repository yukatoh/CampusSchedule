package com.katoh.campusschedule.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.katoh.campusschedule.models.entity.CourseRealmObject
import io.realm.RealmResults
import io.realm.Sort
import io.realm.internal.SortDescriptor

class SortViewModel : ViewModel() {

    enum class SortItem(var isAscending: Boolean) {
        DAY_ORDER(true),
        COURSE_NAME(true),
        TYPE(true),
        POINT(true)
    }

    // Pairs of the name and SortItem object
    private val sortItems = mapOf(
        SortItem.DAY_ORDER.name to SortItem.DAY_ORDER,
        SortItem.COURSE_NAME.name to SortItem.COURSE_NAME,
        SortItem.TYPE.name to SortItem.TYPE,
        SortItem.POINT.name to SortItem.POINT
    )

    // Live data of a selected SortItem Object
    private val _focusedItemData: MutableLiveData<SortItem> =
        MutableLiveData<SortItem>().apply { value = sortItems[SortItem.DAY_ORDER.name] }
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
        _focusedItemData.value = sortItems[name]?.apply {
            isAscending = true
        } ?: throw IllegalArgumentException()

    }

    // Live data of a realm results storing course objects
    private val _courseResultsData: MutableLiveData<RealmResults<CourseRealmObject>> =
        MutableLiveData()
    val courseResultsData: LiveData<RealmResults<CourseRealmObject>>
        get() = _courseResultsData
    val courseResults: RealmResults<CourseRealmObject>
        get() = _courseResultsData.value
            ?: throw  Exception("Cannot get courseResultsData")

    fun updateResults(results: RealmResults<CourseRealmObject>) {
        _courseResultsData.value = sorted(results)
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
        return when (focusedItem.name) {
            SortItem.DAY_ORDER.name -> {
                realmResults.sort(arrayOf("day", "order"), arrayOf(order, order))
            }
            SortItem.COURSE_NAME.name -> {
                realmResults.sort("courseName", order)
            }
            SortItem.TYPE.name -> {
                realmResults.sort("type", order)
            }
            SortItem.POINT.name -> {
                realmResults.sort("point", order)
            }
            else -> throw Exception(
                "selectedItem.name: %s is invalid".format(focusedItem.name)
            )
        }
    }

}