package com.certified.order.model

import com.google.firebase.auth.FirebaseAuth

//TODO: Add a date field
data class Order(
    val receiver_phone_no: String = "",
    val subtotal: Int = 0,
    val items: List<Item>? = null
) {
    var id: String = ""
    val receiver_name = FirebaseAuth.getInstance().currentUser?.displayName
    var receiver_photourl: String? = FirebaseAuth.getInstance().currentUser?.photoUrl.toString()
    var receiver_id = FirebaseAuth.getInstance().currentUser?.uid
    var delivery_time = "00:00"
    var is_delivered = false
    var latitude: String? = null
    var longitude: String? = null
    var rated = false
    var dispatcher_name = ""
    var dispatcher_phone_no = ""
    var dispatcher_photourl: String? = ""
    var status = if (is_delivered) "Delivered" else "Pending"
}