package com.katoh.campusschedule.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.view.ActionMode
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.katoh.campusschedule.R
import com.katoh.campusschedule.fragments.dialogs.DeleteDialogFragment
import com.katoh.campusschedule.models.entity.TypeContent
import com.katoh.campusschedule.models.prefs.CustomSharedPreferences
import com.katoh.campusschedule.models.prefs.PreferenceNames
import com.katoh.campusschedule.utils.settingDao
import com.katoh.campusschedule.viewmodels.RealmResultViewModel
import com.katoh.campusschedule.viewmodels.SortViewModel
import com.katoh.campusschedule.views.actionbar.TimeListActionModeCallback
import com.katoh.campusschedule.views.adapters.TimeSelectableAdapter
import com.katoh.campusschedule.views.adapters.TypeInfoRecyclerAdapter
import kotlinx.android.synthetic.main.fragment_time_list.view.*

class TimeListFragment : CustomFragment() {
        // Views
    private lateinit var timeRecyclerView: RecyclerView
    private lateinit var timeAdapter: TimeSelectableAdapter

    private lateinit var typeRecyclerView: RecyclerView
    private lateinit var typeAdapter: TypeInfoRecyclerAdapter

    private var actionMode: ActionMode? = null
    private val actionModeCallback = TimeListActionModeCallback()

    // View Models
    private val realmViewModel: RealmResultViewModel by activityViewModels()
    private val sortViewModel: SortViewModel by activityViewModels()

    // Dialogs
    private val deleteDialogFragment = DeleteDialogFragment()

    // Shared Preferences
    private val defaultPreferences: CustomSharedPreferences by lazy {
        CustomSharedPreferences(activity, PreferenceNames.DEFAULT)
    }

    /**
     * Get the type contents, saved in shared preferences
     * or default ones of array resources
     */
    private val savedTypeContents: List<TypeContent> by lazy {
        defaultPreferences.settingDao().savedTypeContents
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set Event Listener
        actionModeCallback.setActionModeListener(
            object : TimeListActionModeCallback.ActionModeListener {
                override fun onDestroyActionMode() {
                    timeAdapter.clearSelection()
                    actionMode = null
                    activity.supportActionBar?.show()
                }

                override fun onActionItemClicked(
                    mode: ActionMode?, item: MenuItem?): Boolean {
                    return when (item?.itemId) {
                        R.id.delete -> {
                            // Activate dialog fragment
                            deleteDialogFragment.show(
                                parentFragmentManager, DeleteDialogFragment.TAG_CAM)
                            true
                        }
                        else -> false
                    }
                }
            })

        deleteDialogFragment.setNoticeDialogListener(
            object : DeleteDialogFragment.NoticeDialogListener {
                override fun onPositiveClick(dialog: DialogFragment) {
                    when (dialog.tag) {
                        DeleteDialogFragment.TAG_POPUP -> {
                            // Initialize the course
                            Log.d("delete-from-popup", realmViewModel.selectedCourse.courseName)
                            realmViewModel.initCourse()
                        }
                        DeleteDialogFragment.TAG_CAM -> {
                            val selectedCourses = timeAdapter.getSelectedItemPositions()
                                .map { position ->
                                    sortViewModel.courseResults[position]
                                        ?: throw Exception()
                                }
                            selectedCourses.forEach { course ->
                                realmViewModel.chooseSelectedCourse(course.day, course.order)
                                Log.d("delete-from-cam", course.courseName)
                                realmViewModel.initCourse()
                            }
                            actionMode?.finish()
                        }
                    }
                }
            })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.fragment_time_list, container, false)

        // Recycler View
        timeRecyclerView = view.recyclerview_time.apply {
            layoutManager = LinearLayoutManager(context,
                LinearLayoutManager.VERTICAL, false)
            itemAnimator = DefaultItemAnimator()
        }

        typeRecyclerView = view.recyclerview_type.apply {
            layoutManager = LinearLayoutManager(context,
                LinearLayoutManager.VERTICAL, false)
            itemAnimator = DefaultItemAnimator()
            addItemDecoration(DividerItemDecoration(
                context, DividerItemDecoration.VERTICAL))
        }

        setViewAdapter()

        // Sort Items
        view.day_order.setOnClickListener {
            sortViewModel.updateSortItem(SortViewModel.SortItem.DAY_ORDER.name)
        }
        view.course_name.setOnClickListener {
            sortViewModel.updateSortItem(SortViewModel.SortItem.COURSE_NAME.name)
        }
        view.type.setOnClickListener {
            sortViewModel.updateSortItem(SortViewModel.SortItem.TYPE.name)
        }
        view.point.setOnClickListener {
            sortViewModel.updateSortItem(SortViewModel.SortItem.POINT.name)
        }

        // Other Views
        view.text_course_sum.text = realmViewModel.courseResults.size.toString()
        view.text_point_sum.text = realmViewModel.courseResults.sum("point").toString()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        realmViewModel.selectedCourseData.observe(viewLifecycleOwner, Observer { course ->
            setViewAdapter()
            view.text_course_sum.text = realmViewModel.courseResults.size.toString()
            view.text_point_sum.text = realmViewModel.courseResults.sum("point").toString()
            actionMode?.finish()
        })

        sortViewModel.focusedItemData.observe(viewLifecycleOwner, Observer { item ->
            view.day_order_sort.visibility = View.INVISIBLE
            view.course_name_sort.visibility = View.INVISIBLE
            view.type_sort.visibility = View.INVISIBLE
            view.point_sort.visibility = View.INVISIBLE

            when (item) {
                SortViewModel.SortItem.DAY_ORDER -> {
                    setSortImageView(view.day_order_sort, item.isAscending)
                }
                SortViewModel.SortItem.COURSE_NAME -> {
                    setSortImageView(view.course_name_sort, item.isAscending)
                }
                SortViewModel.SortItem.TYPE -> {
                    setSortImageView(view.type_sort, item.isAscending)
                }
                SortViewModel.SortItem.POINT -> {
                    setSortImageView(view.point_sort, item.isAscending)
                }
                null -> {
                    setSortImageView(view.day_order_sort, true)
                }
            }

            setViewAdapter()
            actionMode?.finish()
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        actionMode?.finish()
    }

    /**
     * Set recycler view adapter, and show the view
     * to generate recycler view
     */
    private fun setViewAdapter() {
        /* TimeSelectableAdapter */
        sortViewModel.updateCourseResults(realmViewModel.courseResults)
        timeAdapter = TimeSelectableAdapter(requireContext(),
            sortViewModel.courseResults, savedTypeContents)

        // Set Event Listener
        timeAdapter.setOnItemClickListener(
            object : TimeSelectableAdapter.OnItemClickListener {
                override fun onClick(position: Int) {
                    if (actionMode != null) {
                        toggleSelection(position)
                    } else {
                        actionMode?.finish()
                        // Update view model
                        val course = sortViewModel.courseResults[position]
                            ?: throw Exception("The course not found")
                        realmViewModel.chooseSelectedCourse(course.day, course.order)

                        // Replace Fragment
                        realmViewModel.initBook()
                        replaceParentFragment(
                            R.id.container_main, CourseSettingFragment()
                        )
                    }
                }

                override fun onLongClick(position: Int): Boolean {
                    if (actionMode == null) {
                        // Activate Context Action Menu (CAM)
                        actionMode = activity.startSupportActionMode(actionModeCallback)
                        activity.supportActionBar?.hide()
                        toggleSelection(position)
                    }
                    return true
                }

            })

        timeAdapter.setOnMenuItemClickListener(
            object : TimeSelectableAdapter.OnMenuItemClickListener {
                override fun onClick(menuItem: MenuItem, position: Int): Boolean {
                    // Update view model
                    val course = sortViewModel.courseResults[position]
                        ?: throw Exception("The course not found")
                    realmViewModel.chooseSelectedCourse(course.day, course.order)
                    return when (menuItem.itemId) {
                        R.id.edit -> {
                            // Replace fragment
                            realmViewModel.initBook()
                            replaceParentFragment(
                                R.id.container_main, CourseSettingFragment()
                            )
                            true
                        }
                        R.id.delete -> {
                            // Activate dialog fragment
                            deleteDialogFragment.show(
                                parentFragmentManager, DeleteDialogFragment.TAG_POPUP
                            )
                            true
                        }
                        else -> false
                    }
                }
            })

        // Set recycler view adapter
        timeRecyclerView.adapter = timeAdapter


        /* TypeRecyclerAdapter */
        typeAdapter = TypeInfoRecyclerAdapter(requireContext(),
            realmViewModel.courseResults, savedTypeContents)

        // Set recycler view adapter
        typeRecyclerView.adapter = typeAdapter
    }

    /**
     * Toggle the selection status of the item at a given position
     */
    private fun toggleSelection(position: Int) {
        timeAdapter.toggleSelection(position)
        val count = timeAdapter.selectedItemCount
        if (count == 0) {
            actionMode?.finish()
        } else {
            actionMode?.title = count.toString()
            actionMode?.invalidate()
        }
    }

    /**
     * Set an image to a focused sort item
     */
    private fun setSortImageView(imgView: ImageView, isAscending: Boolean) {
        imgView.visibility = View.VISIBLE
        if (isAscending) {
            imgView.setImageResource(R.drawable.ic_ascending_black_12dp)
        } else {
            imgView.setImageResource(R.drawable.ic_descending_black_12dp)
        }
    }

}