package com.certified.order.model

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

//TODO: Add a date field
data class Review(
    val reviewer: String = "",
    val review: String = "",
    val reviewer_photourl: String? = Firebase.auth.currentUser!!.photoUrl.toString(),
    val rating: Int = 0
) {
    var id: String = ""
}