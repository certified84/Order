package com.certified.order.model

import android.net.Uri
import java.lang.Math.random
import kotlin.random.Random

data class Item(val id: Int, val name: String, val images: List<Uri>?, val description: String) {
    var quantity = "1"
    val price = Random.nextDouble(450.00, 500.00)
    val total_price = quantity.toInt() * price
}