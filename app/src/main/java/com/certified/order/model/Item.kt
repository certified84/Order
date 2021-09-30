package com.certified.order.model

import kotlin.random.Random

data class Item(val id: Int, val name: String, val description: String, val picture: Int) {
    var quantity = "1"
    val price = Random.nextInt(450, 500)
    val total_price = quantity.toInt() * price
}