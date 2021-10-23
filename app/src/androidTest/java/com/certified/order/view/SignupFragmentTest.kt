package com.certified.order.view

import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.certified.order.R
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.junit.After
import org.junit.Before
import org.junit.Test

class SignupFragmentTest {

    private lateinit var scenario: FragmentScenario<SignupFragment>

    @Before
    fun setUp() {
        scenario = launchFragmentInContainer(themeResId = R.style.Theme_Order)
        scenario.moveToState(Lifecycle.State.STARTED)
    }

    @Test
    fun signUpUser() {
        val name = "Samson Achiaga"
        val email = "Sammie_kt@pm.me"
        val phone = "+2348136108482"
        val password = "certified84"

        onView(withId(R.id.et_name)).perform(typeText(name))
        onView(withId(R.id.et_name)).perform(typeText(email))
        onView(withId(R.id.et_name)).perform(typeText(phone))
        onView(withId(R.id.et_name)).perform(typeText(password))

        Espresso.closeSoftKeyboard()
        onView(withId(R.id.btn_login)).perform(click())
    }

    @After
    fun tearDown() {
    }
}