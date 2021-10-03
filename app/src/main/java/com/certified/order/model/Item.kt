package com.certified.order.model

import kotlin.random.Random

data class Item(val name: String = "", val description: String = "", var type: String = "") {
    var id: String = ""
    var quantity = "1"
    var price = Random.nextInt(450, 500)
    var total_price = quantity.toInt() * price
}