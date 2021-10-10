package com.certified.order.model

import android.location.Address
import android.net.Uri
import com.certified.order.R

data class User(
    val name: String,
    val phone: String
) {
    val account_type = "user"
    val profile_image: String? = null
    var email = ""
    val default_Address: Address? = null
    val default_address_line = "Click here to set delivery address"
    val default_address_latitude = default_Address?.latitude
    val default_address_longitude = default_Address?.longitude
    var id = ""
}