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
    val receiver_id = FirebaseAuth.getInstance().currentUser?.uid
    var deliveryTime = "00:00"
    var isDelivered = false
    val latitude: String? = null
    val longitude: String? = null
    var isRated = false
    val dispatcher_name = ""
    val dispatcher_phone_no = ""
    val status = if (isDelivered) "Delivered" else "Pending"
}