package com.certified.order.model

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

data class Review(
    val reviewer: String = "",
    val review: String = "",
    val reviewer_photoUrl: String? = Firebase.auth.currentUser!!.photoUrl.toString(),
    val rating: Int = 0
) {
    var id: String = ""
}