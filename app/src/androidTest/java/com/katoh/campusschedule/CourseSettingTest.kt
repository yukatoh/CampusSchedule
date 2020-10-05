package com.katoh.campusschedule

import android.view.View
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.katoh.campusschedule.activities.MainActivity
import com.katoh.campusschedule.models.entity.BookContent
import com.katoh.campusschedule.models.entity.CourseRealmObject
import com.katoh.campusschedule.views.adapters.StartSelectableAdapter
import org.hamcrest.Description
import org.hamcrest.Matchers.anything
import org.hamcrest.TypeSafeMatcher
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CourseSettingTest {

    @get:Rule
    var activityRule: ActivityScenarioRule<MainActivity> =
        ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun createTerm() {
        TermSettingTest().createTerm()
    }

    @Test
    fun editCourse() {
        openTerm()
        // Open course setting
        onView(withTag(listOf(SAMPLE_COURSE.day, SAMPLE_COURSE.order)))
            .perform(click())

        // Edit text
        onView(withId(R.id.edit_course))
            .perform(clearText(), typeText(SAMPLE_COURSE.courseName), closeSoftKeyboard())
        onView(withId(R.id.edit_teacher))
            .perform(clearText(), typeText(SAMPLE_COURSE.teacherName), closeSoftKeyboard())
        onView(withId(R.id.edit_point))
            .perform(clearText(), typeText(SAMPLE_COURSE.point.toString()), closeSoftKeyboard())
        onView(withId(R.id.edit_grade))
            .perform(clearText(), typeText(SAMPLE_COURSE.grade.toString()), closeSoftKeyboard())

        // Select in the type spinner
        onView(withId(R.id.spinner_type)).perform(click())
        onData(anything()).atPosition(SAMPLE_COURSE.type).perform(click())

        // Click SAVE button
        onView(withId(R.id.save)).perform(click())

        // Check course
        onView(withTag(listOf(SAMPLE_COURSE.day, SAMPLE_COURSE.order)))
            .check(matches(withText(SAMPLE_COURSE.courseName)))

    }

    private fun openTerm() {
        // Click a recycler view item
        onView(withId(R.id.recyclerview_start))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<
                    StartSelectableAdapter.StartViewHolder>(
                0, click()))
        // Check term opened
        onView(withText(SAMPLE_TERM_NAME))
            .check(matches(isDisplayed()))
    }

    private fun withTag(tag: Any): TypeSafeMatcher<View> =
        object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description?) {
                description?.appendText("tag is not equal to $tag")
            }

            override fun matchesSafely(item: View?): Boolean =
                if (item != null) item.tag == tag
                else false

        }

    companion object {
        const val SAMPLE_TERM_NAME = TermSettingTest.SAMPLE_TERM_NAME

        private val SAMPLE_BOOK = BookContent(
            title = "Flowers for Algernon",
            author = "Daniel Keyes",
            publisher = "Houghton Mifflin Harcourt"
        )

        val SAMPLE_COURSE = CourseRealmObject().apply {
            day = 2         // Tuesday
            order = 3       // 4th period
            courseName = "Literature"
            teacherName = "John Smith"
            type = 1
            point = 2
            grade = 3
            textbook = SAMPLE_BOOK.joinToString()
            bookTitle = SAMPLE_BOOK.title
            bookAuthor = SAMPLE_BOOK.author
            bookPublisher = SAMPLE_BOOK.publisher
            email = ""
            url = ""
            additional = ""
        }

    }
}