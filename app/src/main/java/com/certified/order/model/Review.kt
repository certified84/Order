package com.certified.order.model

import com.google.firebase.auth.FirebaseUser

data class Review(
    val reviewer: FirebaseUser,
    val review: String,
    val rating: Float
)