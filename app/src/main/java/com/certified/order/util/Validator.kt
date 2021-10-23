package com.certified.order.util

import android.util.Patterns

object Validator {

    fun isFieldEmpty(text: String): Boolean = text.isEmpty()
    fun isEmailValid(text: String): Boolean {return Patterns.EMAIL_ADDRESS.matcher(text).matches()}
    fun isPasswordValid(text: String): Boolean = text.length >= 8
    fun isFieldNotEmpty(text: String): Boolean = text.isNotEmpty()
}