package com.certified.order.model

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth

data class Order(
    val receiver_name: String = "",
    val receiver_photourl: Uri? = null,
    val receiver_phone_no: String = "",
    val subtotal: Double = 0.0,
    val items: List<Item>? = null
) {
    var id: String = ""
    var receiver_id = FirebaseAuth.getInstance().currentUser?.uid
    var deliveryTime = "00:00"
    var isDelivered = false
    var latitude: String? = null
    var longitude: String? = null
    var isRated = false
    var dispatcher_name = ""
    var dispatcher_phone_no = ""
    var status = if (isDelivered) "Delivered" else "Pending"
}