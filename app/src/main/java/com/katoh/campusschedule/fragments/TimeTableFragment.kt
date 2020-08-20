package com.katoh.campusschedule.fragments

import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.RippleDrawable
import android.graphics.drawable.StateListDrawable
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.PopupMenu
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
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
import com.katoh.campusschedule.viewmodels.CustomResultViewModel
import kotlinx.android.synthetic.main.fragment_time_table2.view.*

class TimeTableFragment : CustomFragment() {
    private val model: CustomResultViewModel by activityViewModels()
    private val activity: AppCompatActivity by lazy {
        getActivity() as AppCompatActivity
    }
    private val sp: CustomSharedPreferences by lazy {
        CustomSharedPreferences(activity, PreferenceNames.DEFAULT)
    }

    private val savedItem: SettingDao.SavedItem by lazy {
        SettingDao.SavedItem(
            typeContents = sp.settingDao().savedTypeContents,
            satVisible = sp.settingDao().satVisible,
            timeOrderMax = sp.settingDao().timeOrderMax
        )
    }

    /**
     * Get a type content (label & color) according to the selected course type
     */
    private fun getUtilTypeContent(course: CourseRealmObject): TypeContent =
        savedItem.getUtilTypeContent(course,
            TypeContent("", requireContext().getColor(R.color.white)))

    private val isTablet: Boolean by lazy {
        resources.getBoolean(R.bool.is_tablet)
    }
    private val deleteDialogFragment = DeleteDialogFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        deleteDialogFragment.setNoticeDialogListener(
            object : DeleteDialogFragment.NoticeDialogListener {
                override fun onPositiveClick(dialog: DialogFragment) {
                    model.initCourse()
                }
            })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.fragment_time_table2, container, false)

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
        activity.supportActionBar?.title = model.selectedTerm.termLabel

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // view model
        model.selectedCourseData.observe(viewLifecycleOwner, Observer { course ->
            view.findViewWithTag<TextView>(
                listOf(course.day, course.order)
            ).run {
                updateContentTextProperty(course)
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
                model.chooseSelectedCourse(day, i)
                val contentView = TextView(context).apply {
                    // View Property
                    tag = listOf(model.selectedCourse.day, model.selectedCourse.order)

                    updateContentTextProperty(model.selectedCourse)

                    // Event Listener
                    setOnClickListener {
                        model.chooseSelectedCourse(day, i)
                        Log.d("courseName", model.selectedCourse.courseName)
                        // Replace fragment
                        replaceParentFragment(R.id.container_main, CourseSettingFragment())
                    }

                    setOnLongClickListener { v ->
                        v.isSelected = true
                        model.chooseSelectedCourse(day, i)
                        PopupMenu(context, v).apply {
                            inflate(R.menu.menu_time_table_popup)
                            gravity = Gravity.END
                            setOnMenuItemClickListener {
                                when (it.itemId) {
                                    R.id.edit -> {
                                        // Replace fragment
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
                row.addView(contentView, contentParams)
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
                    requireContext().getColor(R.color.green_light)),
                    null, null)
            ))
        )
        return drawable
    }

    private fun TextView.updateContentTextProperty(course: CourseRealmObject) {
        this.apply {
            val type = getUtilTypeContent(course)
            if (isTablet) {
                text =
                    if (type.label.isBlank()) ""
                    else """
                                ${type.label}
                                ${course.courseName}
                                ${course.point}${getString(R.string.point)}
                                """.trimIndent()
                setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    resources.getDimension(R.dimen.font_medium))

            }
            else {
                text = model.selectedCourse.courseName
                setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    resources.getDimension(R.dimen.font_small))
            }

            setTextColor(context.getTextColorFromBg(type.color))
            background = getCustomBackground(type.color)

        }
    }

}