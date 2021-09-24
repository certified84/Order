package com.certified.order.model

import android.net.Uri
import com.certified.order.R

data class User(
    val name: String,
    val phone: String
) {
    val account_type = "user"
    val profile_image: Uri? = null
    var email = ""
    var id = ""
}