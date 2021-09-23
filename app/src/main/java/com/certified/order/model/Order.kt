package com.certified.order.model

import com.google.firebase.auth.FirebaseUser
import com.google.type.LatLng

data class Order(
    val receiver: FirebaseUser,
    val receiver_address: LatLng,
    val receiver_phone_no: Long,
    val items: List<Item>
) {
    val isDelivered = false
    val isRated = false
}