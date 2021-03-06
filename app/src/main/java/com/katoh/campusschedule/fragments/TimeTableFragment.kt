package com.katoh.campusschedule.fragments

import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.RippleDrawable
import android.graphics.drawable.StateListDrawable
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.katoh.campusschedule.R
import com.katoh.campusschedule.fragments.dialogs.DeleteDialogFragment
import com.katoh.campusschedule.models.dao.SettingDao
import com.katoh.campusschedule.models.entity.CourseRealmObject
import com.katoh.campusschedule.models.entity.TypeContent
import com.katoh.campusschedule.models.prefs.CustomSharedPreferences
import com.katoh.campusschedule.models.prefs.PreferenceNames
import com.katoh.campusschedule.utils.getTextColorFromBg
import com.katoh.campusschedule.utils.settingDao
import com.katoh.campusschedule.viewmodels.RealmResultViewModel
import kotlinx.android.synthetic.main.fragment_time_table.view.*

class TimeTableFragment : CustomFragment() {
    // View Models
    private val realmViewModel: RealmResultViewModel by activityViewModels()

    // Shared Preferences
    private val defaultPreferences: CustomSharedPreferences by lazy {
        CustomSharedPreferences(activity, PreferenceNames.DEFAULT)
    }

    // Dialogs
    private val deleteDialogFragment = DeleteDialogFragment()

    /**
     * Data class of items saved in shared preferences
     */
    private val savedItem: SettingDao.SavedItem by lazy {
        SettingDao.SavedItem(
            typeContents = defaultPreferences.settingDao().savedTypeContents,
            satVisible = defaultPreferences.settingDao().satVisible,
            timeOrderMax = defaultPreferences.settingDao().timeOrderMax
        )
    }

    /**
     * Whether the device is tablet or not
     */
    private val isTablet: Boolean by lazy {
        resources.getBoolean(R.bool.is_tablet)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Dialogs
        deleteDialogFragment.setNoticeDialogListener(
            object : DeleteDialogFragment.NoticeDialogListener {
                override fun onPositiveClick(dialog: DialogFragment) {
                    realmViewModel.initCourse()
                }
            })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.fragment_time_table, container, false)

        // Saturday visibility
        view.text_sat.visibility =
            if (savedItem.satVisible) View.VISIBLE
            else View.GONE
        view.border_v06.visibility =
            if (savedItem.satVisible) View.VISIBLE
            else View.GONE

        val layout: TableLayout = view.findViewById(R.id.time_table_layout)
        createTableLayout(layout)

        // Action Bar
        activity.supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
            title = realmViewModel.selectedTerm.termLabel
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // view model
        realmViewModel.selectedCourseData.observe(viewLifecycleOwner, Observer { course ->
            view.findViewWithTag<TextView>(
                listOf(course.day, course.order)
            ).run {
                updatePeriodViewProperty(course)
            }

        })
    }

    private fun createTableLayout(layout: TableLayout) {
        for (i in 1..savedItem.timeOrderMax)  {
            // Add table row
            val row = TableRow(context).apply {
                gravity = Gravity.CENTER_VERTICAL
            }
            val columnParams = TableLayout.LayoutParams().apply { weight = 1F }
            layout.addView(row, columnParams)

            // Add text view at column position 0
            val iTextView = TextView(context).apply {
                text = "$i"
                gravity = Gravity.CENTER
                setTextAppearance(R.style.TextAppearance_AppCompat_Large)
            }
            val iParams = TableRow.LayoutParams(
                0, TableRow.LayoutParams.MATCH_PARENT, 0.25F)
            row.addView(iTextView, iParams)

            val daysLength = if (savedItem.satVisible) 6 else 5
            for (day in 0 until daysLength) {
                // Add vertical line
                val vBorderView = View(context).apply {
                    setBackgroundResource(R.color.colorPrimaryDark)
                }
                row.addView(vBorderView,
                    resources.getDimensionPixelSize(R.dimen.border_width_normal),
                    ViewGroup.LayoutParams.MATCH_PARENT)

                // Add text view to show the course context
                realmViewModel.chooseSelectedCourse(day, i)
                val periodView = TextView(context).apply {
                    // View Property
                    tag = listOf(realmViewModel.selectedCourse.day, realmViewModel.selectedCourse.order)

                    updatePeriodViewProperty(realmViewModel.selectedCourse)

                    // Event Listener
                    setOnClickListener {
                        // Update view model
                        realmViewModel.chooseSelectedCourse(day, i)
                        Log.d("courseName", realmViewModel.selectedCourse.courseName)

                        // Replace fragment
                        realmViewModel.initBook()
                        Log.d("model-bookTitle", realmViewModel.selectedCourse.bookTitle)
                        replaceParentFragment(R.id.container_main, CourseSettingFragment())
                    }

                    setOnLongClickListener { v ->
                        v.isSelected = true
                        // Update view model
                        realmViewModel.chooseSelectedCourse(day, i)

                        PopupMenu(context, v).apply {
                            inflate(R.menu.menu_time_table_popup)
                            gravity = Gravity.END
                            setOnMenuItemClickListener {
                                when (it.itemId) {
                                    R.id.edit -> {
                                        // Replace fragment
                                        realmViewModel.initBook()
                                        this@TimeTableFragment.replaceParentFragment(
                                            R.id.container_main, CourseSettingFragment()
                                        )
                                        true
                                    }
                                    R.id.delete -> {
                                        // Activate dialog fragment
                                        deleteDialogFragment.show(
                                            parentFragmentManager, DeleteDialogFragment.TAG_POPUP)
                                        true
                                    }
                                    else -> false
                                }
                            }
                            setOnDismissListener {
                                v.isSelected = false
                            }
                            show()
                        }
                        true
                    }
                }

                val contentParams = TableRow.LayoutParams(
                    0, TableRow.LayoutParams.MATCH_PARENT, 1F)
                row.addView(periodView, contentParams)
            }

            // Add vertical line
            val vBorderView = View(context).apply {
                setBackgroundResource(R.color.colorPrimaryDark)
            }
            row.addView(vBorderView,
                resources.getDimensionPixelSize(R.dimen.border_width_normal),
                ViewGroup.LayoutParams.MATCH_PARENT)

            // Add horizontal line
            val hBorderView = View(context).apply {
                setBackgroundResource(R.color.colorPrimaryDark)
            }
            layout.addView(hBorderView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                resources.getDimensionPixelSize(R.dimen.border_width_normal))
        }

    }

    private fun getCustomBackground(backgroundColor: Int): StateListDrawable {
        val drawable = StateListDrawable()
        drawable.addState(intArrayOf(android.R.attr.state_selected),
            GradientDrawable().apply {
                setColor(backgroundColor)
                setStroke(
                    resources.getDimensionPixelSize(R.dimen.border_width_normal),
                    requireContext().getColor(R.color.colorAccent)
                )
            })

        drawable.addState(intArrayOf(),
            LayerDrawable(arrayOf(
                GradientDrawable().apply {
                    setColor(backgroundColor)
                },
                RippleDrawable(ColorStateList.valueOf(
                    requireContext().getColor(R.color.colorControlHighlight)),
                    null, null)
            ))
        )
        return drawable
    }

    /**
     * Get a type content (label & color) according to the selected course type
     */
    private fun getUtilTypeContent(course: CourseRealmObject): TypeContent =
        savedItem.getUtilTypeContent(course,
            TypeContent("", requireContext().getColor(R.color.white)))

    /**
     * Update a text view property of the table periods,
     * text, textSize, textColor, background
     */
    private fun TextView.updatePeriodViewProperty(course: CourseRealmObject) {
        this.apply {
            val type = getUtilTypeContent(course)
            if (isTablet) {
                text =
                    if (type.label.isBlank()) ""
                    else
                        """
                        ${type.label}
                        ${course.courseName}
                        ${course.point}${getString(R.string.point)}
                        """.trimIndent()

                setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    resources.getDimension(R.dimen.font_medium))

            }
            else {
                text = course.courseName
                setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    resources.getDimension(R.dimen.font_small))
            }

            setTextColor(context.getTextColorFromBg(type.color))
            background = getCustomBackground(type.color)

        }
    }

}