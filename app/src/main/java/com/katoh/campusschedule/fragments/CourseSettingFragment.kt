package com.katoh.campusschedule.fragments

import android.os.Bundle
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.katoh.campusschedule.R
import com.katoh.campusschedule.fragments.dialogs.BookSettingDialogFragment
import com.katoh.campusschedule.models.entity.BookContent
import com.katoh.campusschedule.models.entity.CourseRealmObject
import com.katoh.campusschedule.models.entity.TypeContent
import com.katoh.campusschedule.models.prefs.CustomSharedPreferences
import com.katoh.campusschedule.models.prefs.PreferenceNames
import com.katoh.campusschedule.utils.settingDao
import com.katoh.campusschedule.viewmodels.RealmResultViewModel
import com.katoh.campusschedule.views.adapters.TypeSpinnerAdapter
import kotlinx.android.synthetic.main.fragnent_course_setting.*
import kotlinx.android.synthetic.main.fragnent_course_setting.view.*

class CourseSettingFragment : CustomFragment() {
    // View Models
    private val viewModel: RealmResultViewModel by activityViewModels()

    // Shared Preferences
    private val defaultPreferences: CustomSharedPreferences by lazy {
        CustomSharedPreferences(activity, PreferenceNames.DEFAULT)
    }

    // Dialogs
    private val bookSettingDialogFragment = BookSettingDialogFragment()

    /**
     * Get the type contents, saved in shared preferences
     * or default ones of array resources
     */
    private val savedTypeContents: List<TypeContent> by lazy {
        defaultPreferences.settingDao().savedTypeContents
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        // Action Bar
        activity.supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
            title = "%s %s".format(
                translateWeekDay(viewModel.selectedCourse.day),
                viewModel.selectedCourse.order
            )
        }

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
            edit_course.setText(viewModel.selectedCourse.courseName)
            edit_teacher.setText(viewModel.selectedCourse.teacherName)
            edit_point.setText(viewModel.selectedCourse.point.toString())
            edit_grade.setText(viewModel.selectedCourse.grade.toString())
            edit_book.text = viewModel.tempBook.joinToString()
            edit_email.setText(viewModel.selectedCourse.email)
            edit_url.setText(viewModel.selectedCourse.url)
            edit_additional.setText(viewModel.selectedCourse.additional)
        }

        view.spinner_type.run {
            val adapter = TypeSpinnerAdapter(context, savedTypeContents)
            adapter.insert(TypeContent(
                getString(R.string.unselected_text),
                context.getColor(R.color.white)
            ), 0)
            setAdapter(adapter)
            setSelection(viewModel.selectedCourse.type + 1)
        }

        view.button_edit_book.setOnClickListener {
            bookSettingDialogFragment.show(
                parentFragmentManager, BookSettingDialogFragment.TAG_DEFAULT)
        }

        bookSettingDialogFragment.setNoticeDialogListener(
            object : BookSettingDialogFragment.NoticeDialogListener {
                override fun onPositiveClick(dialog: DialogFragment, book: BookContent) {
                    view.edit_book.text = book.joinToString()
                }
            })

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
                            textbook = viewModel.tempBook.joinToString()
                            bookTitle = viewModel.tempBook.title
                            bookAuthor = viewModel.tempBook.author
                            bookPublisher = viewModel.tempBook.publisher
                            email = edit_email.text.toString()
                            url = edit_url.text.toString()
                            additional = edit_additional.text.toString()
                        }
                        viewModel.updateCourseSetting(course)
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