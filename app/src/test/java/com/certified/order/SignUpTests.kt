package com.certified.order

import com.certified.order.util.Validator
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for signup a user
 */
class SignUpTests {

    lateinit var email: String
    lateinit var password: String
    lateinit var name: String
    lateinit var phone: String

    @Before
    fun setUP() {
        email = "achiagasamson5@gmail.com"
        password = "certified84"
        name = "Samson Achiaga"
        phone = "+2348136108482"
    }

    @Test
    fun is_email_field_not_empty() {
        assertThat(Validator.isFieldEmpty(email)).isFalse()
    }

    @Test
    fun is_email_field_empty() {
        email = ""
        assertThat(Validator.isFieldEmpty(email)).isTrue()
    }

    @Test
    fun is_password_field_not_empty() {
        assertThat(Validator.isFieldEmpty(password)).isFalse()
    }

    @Test
    fun is_password_field_empty() {
        password = ""
        assertThat(Validator.isFieldEmpty(password)).isEqualTo(true)
    }

    @Test
    fun is_name_field_not_empty() {
        assertThat(Validator.isFieldEmpty(name)).isEqualTo(false)
    }

    @Test
    fun is_name_field_empty() {
        name = ""
        assertThat(Validator.isFieldEmpty(name)).isEqualTo(true)
    }

    @Test
    fun is_phone_field_not_empty() {
        assertThat(Validator.isFieldEmpty(name)).isEqualTo(false)
    }

    @Test
    fun is_phone_field_empty() {
        phone = ""
        assertThat(Validator.isFieldEmpty(phone)).isEqualTo(true)
    }

    @Test
    fun is_email_valid() {
        println(Validator.isEmailValid(email))
        assertThat(Validator.isEmailValid(email)).isTrue()
    }

    @Test
    fun `is the email invalid`() {
        email = ""
        assertThat(Validator.isEmailValid(email)).isFalse()
    }

    @Test
    fun is_password_valid() {
        assertThat(Validator.isPasswordValid(password)).isEqualTo(true)
    }

    @Test
    fun is_password_not_valid() {
        password = "sammie"
        assertThat(Validator.isPasswordValid(password)).isEqualTo(false)
    }

    @Test
    fun is_name_first_and_last() {

    }
}