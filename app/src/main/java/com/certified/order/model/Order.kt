package com.certified.order.model

import android.location.Address
import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

data class Order(
    val receiver_name: String,
    val receiver_photourl: Uri?,
    val receiver_phone_no: String,
    val subtotal: Double,
    val items: List<Item>
) {
    var id: String = ""
    val receiver_id = FirebaseAuth.getInstance().currentUser?.uid
    var deliveryTime = "00:00"
    var isDelivered = false
    val latitude: String? = null
    val longitude: String? = null
    val isRated = false
    val dispatcher_name = ""
    val dispatcher_phone_no = ""
    val status = if (isDelivered) "Delivered" else "Pending"
}