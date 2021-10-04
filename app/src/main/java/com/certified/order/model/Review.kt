package com.certified.order.model

data class Review(
    val reviewer: String = "",
    val review: String = "",
    val rating: Int = 0
) {
    var id: String = ""
}