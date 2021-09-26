package com.certified.order.model

import android.location.Address
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseUser

data class Order(
    val receiver: FirebaseUser?,
    val dispatcher: Dispatcher?,
    val receiver_address: Address?,
    val receiver_phone_no: String,
    val subtotal: Double,
    val items: List<Burger>
) {
    var delivery_time = "00:00"
    val isDelivered = false
    val isRated = false
}