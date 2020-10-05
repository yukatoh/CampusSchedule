package com.katoh.campusschedule

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.katoh.campusschedule.activities.MainActivity
import com.katoh.campusschedule.utils.CustomViewActions.clickItemWithId
import com.katoh.campusschedule.views.adapters.StartSelectableAdapter
import org.hamcrest.Description
import org.hamcrest.Matchers.anything
import org.hamcrest.Matchers.not
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class TermSettingTest {

    @get:Rule
    var activityRule: ActivityScenarioRule<MainActivity> =
        ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun createTerm() {
        val fab = onView(withId(R.id.fab_start))
        // Click floating action button
        fab.check(matches(isDisplayed())).perform(click())
        // Edit text
        onView(withId(R.id.edit_file_name))
            .perform(typeText(SAMPLE_TERM_NAME), closeSoftKeyboard())
        // Click CREATE button
        onView(withText(R.string.create)).perform(click())
        fab.check(doesNotExist())

        pressBack()
        // Check the created term name
        onView(withId(R.id.recyclerview_start))
            .check(matches(hasText(0, SAMPLE_TERM_NAME)))

    }

    @Test
    fun editTerm() {
        val recyclerView = onView(withId(R.id.recyclerview_start))
        // Click the popup button
        recyclerView.perform(
            RecyclerViewActions.actionOnItemAtPosition<
                    StartSelectableAdapter.StartViewHolder>(
                0, clickItemWithId(R.id.button_popup))
        )

        // Click popup item "Property" at position 2
        onData(anything()).inRoot(RootMatchers.isPlatformPopup())
            .atPosition(2).perform(click())
        // Edit text
        onView(withId(R.id.edit_file_name))
            .perform(clearText(), typeText(EDITED_TERM_NAME), closeSoftKeyboard())
        // Click apply button
        onView(withText(R.string.apply))
            .perform(click())

        // Check the edited term name
        recyclerView.check(matches(hasText(0, EDITED_TERM_NAME)))
    }

    @Test
    fun deleteTerm() {
        val recyclerView = onView(withId(R.id.recyclerview_start))
        // Long click a recycler view item
        recyclerView.perform(
            RecyclerViewActions.actionOnItemAtPosition<
                    StartSelectableAdapter.StartViewHolder>(
                0, longClick())
        )

        // Click delete button
        onView(withId(R.id.delete)).perform(click())
        // Click OK button
        onView(withText(R.string.yes)).perform(click())

        // Check the deleted term name
        recyclerView.check(matches(not(hasText(0, EDITED_TERM_NAME))))
    }

    private fun hasText(position: Int, text: String): TypeSafeMatcher<View> =
        object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description?) {
                description?.appendText("text is not equal to $text")
            }

            override fun matchesSafely(item: View?): Boolean {
                if (item !is RecyclerView) return false

                val holder = item.findViewHolderForAdapterPosition(position)
                        as StartSelectableAdapter.StartViewHolder
                return holder.termName.text == text
            }

        }

    companion object {
        const val SAMPLE_TERM_NAME = "Sample Term"
        const val EDITED_TERM_NAME = "Sample Term 2"
    }
}