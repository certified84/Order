package com.certified.order.model

import com.google.firebase.auth.FirebaseUser

data class Order(val user: FirebaseUser, val phone_no: Long, val items: List<Item>)