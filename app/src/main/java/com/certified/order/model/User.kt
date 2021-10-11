package com.certified.order.model

data class User(
    val name: String,
    val phone: String
) {
    val account_type = "user"
    val profile_image: String? = null
    var email = ""
    val default_address_line = "Click here to set delivery address"
    val default_address_latitude: String? = null
    val default_address_longitude: String? = null
    var id = ""
}