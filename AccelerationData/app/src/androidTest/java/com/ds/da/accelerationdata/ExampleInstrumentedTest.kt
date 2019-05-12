package com.ds.da.accelerationdata

import android.app.PendingIntent.getActivity
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import kotlinx.android.synthetic.main.activity_main.*
import androidx.test.rule.ActivityTestRule
import org.hamcrest.Matchers.not

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Rule
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.Espresso.onView
import org.hamcrest.Matchers.`is`


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@LargeTest
class ExampleInstrumentedTest {
    @get:Rule
    var activityRule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java)

    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getTargetContext()
        assertEquals("com.ds.da.accelerationdata", appContext.packageName)
    }

    @Test
    fun greeterSaysHello() {
        onView(withId(R.id.editTextURL)).perform(typeText("Steve"), closeSoftKeyboard())
        onView(withId(R.id.switchUseService)).perform(click())
        onView(withId(R.id.buttonBindService)).perform(click())
        onView(withId(R.id.buttonSayHello)).perform(click())
        //onView(withText("service starting")).check(matches(isDisplayed()))

      //  onView(withText("hello!"))
        //    .inRoot(withDecorView(not(`is`(activityRule.activity.window.getDecorView()))))
         //   .perform(click()).check((matches(isDisplayed())))

        onView(withText("hello!")).inRoot(withDecorView(not(`is`(activityRule.activity.getWindow().getDecorView()))))
            .check(matches(isDisplayed()))
    }

    @Test
    fun `checkupdatelabels`() {
        val ma = activityRule.activity
        val pre = ma.textViewUpdRateLabel.text
        ma.updateRateLabel()
        val post = ma.textViewUpdRateLabel.text
        assertNotEquals(pre, post)
    }
}