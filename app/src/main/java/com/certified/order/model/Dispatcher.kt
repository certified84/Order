package com.certified.order.model

import android.net.Uri

data class Dispatcher (
    val name: String,
    val phone: String
) {
    val account_type = "dispatcher"
    val profile_image: Uri? = null
    var email = ""
    var id = ""
    var is_available = false
}