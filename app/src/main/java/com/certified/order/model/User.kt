package com.certified.order.model

import android.net.Uri

data class User(
    val name: String,
    val phone: String,
    val account_type: String
) {
    val profileImage: Uri? = null
    var email = ""
    var id = ""
    var approved = false
}