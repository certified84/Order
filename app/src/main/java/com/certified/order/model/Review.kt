package com.certified.order.model

import android.net.Uri

data class Review(
    val reviewer: String = "",
    val review: String = "",
    val reviewer_photoUrl: Uri? = null,
    val rating: Int = 0
) {
    var id: String = ""
}