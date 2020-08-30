package com.katoh.campusschedule.fragments

import android.os.Bundle
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.katoh.campusschedule.R
import com.katoh.campusschedule.models.entity.CourseRealmObject
import com.katoh.campusschedule.models.entity.TypeContent
import com.katoh.campusschedule.models.prefs.CustomSharedPreferences
import com.katoh.campusschedule.models.prefs.PreferenceNames
import com.katoh.campusschedule.utils.settingDao
import com.katoh.campusschedule.viewmodels.RealmResultViewModel
import com.katoh.campusschedule.views.adapters.TypeSpinnerAdapter
import kotlinx.android.synthetic.main.fragnent_course_setting.*
import kotlinx.android.synthetic.main.fragnent_course_setting.view.*

class CourseSettingFragment : Fragment() {
    // Activity
    private val activity: AppCompatActivity by lazy {
        getActivity() as AppCompatActivity
    }

    // View Models
    private val model: RealmResultViewModel by activityViewModels()

    // Shared Preferences
    private val sp: CustomSharedPreferences by lazy {
        CustomSharedPreferences(activity, PreferenceNames.DEFAULT)
    }

    /**
     * Get the type contents, saved values of shared preferences
     * or default ones of array resources
     */
    private val savedTypeContents: List<TypeContent> by lazy {
        sp.settingDao().savedTypeContents
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.fragnent_course_setting, container, false
        ).apply {
            // Set texts from model to EditView
            edit_course.setText(model.selectedCourse.courseName)
            edit_teacher.setText(model.selectedCourse.teacherName)
            edit_point.setText(model.selectedCourse.point.toString())
            edit_grade.setText(model.selectedCourse.grade.toString())
            edit_book.setText(model.selectedCourse.textbook)
            edit_email.setText(model.selectedCourse.email)
            edit_url.setText(model.selectedCourse.url)
            edit_additional.setText(model.selectedCourse.additional)
        }

        view.spinner_type.run {
            val adapter = TypeSpinnerAdapter(context, savedTypeContents)
            adapter.insert(TypeContent(
                getString(R.string.unselected_text),
                context.getColor(R.color.white)
            ), 0)
            setAdapter(adapter)
            setSelection(model.selectedCourse.type + 1)
        }

        // Action Bar
        activity.supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
            title = "%s %s".format(
                translateWeekDay(model.selectedCourse.day),
                model.selectedCourse.order
            )
        }

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_course_setting, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                parentFragmentManager.popBackStack()
                true
            }
            R.id.save -> {
                if (edit_course.text.toString().isBlank()) {
                    edit_course.error = getString(R.string.blank_message_course_name)
                }
                if (edit_point.text.toString().isBlank()) {
                    edit_point.error = getString(R.string.blank_message_point)
                }
                if (edit_grade.text.toString().isBlank()) {
                    edit_grade.error = getString(R.string.blank_message_grade)
                }
                if (spinner_type.selectedItemPosition == 0) {
                    (spinner_type.selectedView as TextView).run {
                        error = ""
                        text = getString(R.string.unselected_text)
                        setTextColor(context.getColor(R.color.design_default_color_error))
                    }
                }
                when {
                    edit_course.text.toString().isBlank() or
                            edit_point.text.toString().isBlank() or
                            edit_grade.text.toString().isBlank() or
                            (spinner_type.selectedItemPosition == 0)
                    -> {
                        Toast.makeText(context,
                            R.string.warning_message_input, Toast.LENGTH_SHORT)
                        super.onOptionsItemSelected(item)
                    }
                    else -> {
                        val course = CourseRealmObject().apply {
                            courseName = edit_course.text.toString()
                            teacherName = edit_teacher.text.toString()
                            type = spinner_type.selectedItemPosition - 1
                            point = edit_point.text.toString().toLong()
                            grade = edit_grade.text.toString().toLong()
                            textbook = edit_book.text.toString()
                            email = edit_email.text.toString()
                            url = edit_url.text.toString()
                            additional = edit_additional.text.toString()
                        }
                        model.updateCourseSetting(course)
                        parentFragmentManager.popBackStack()
                        true
                    }
                }
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Translate the day index to the string resource
     */
    private fun translateWeekDay(day: Int) : String {
        val weekDays = resources.getStringArray(R.array.labels_weekday)
        return weekDays[day]
    }
}