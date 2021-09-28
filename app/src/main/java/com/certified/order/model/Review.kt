package com.certified.order.model

import com.google.firebase.auth.FirebaseUser

data class Review(val id: Int,
    val reviewer: String,
    val review: String,
    val rating: Float
)