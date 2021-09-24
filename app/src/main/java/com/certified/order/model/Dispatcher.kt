package com.certified.order.model

import android.net.Uri
import com.google.type.LatLng

data class Dispatcher (
    val name: String,
    val phone: String
) {
    val account_type = "dispatcher"
    val profile_image: Uri? = null
    var email = ""
    val current_location: LatLng? = null
    var id = ""
    var is_available = false
}