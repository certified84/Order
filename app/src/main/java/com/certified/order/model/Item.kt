package com.certified.order.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlin.random.Random

@Parcelize
data class Item(val name: String = "", val description: String = "", var type: String = "") :
    Parcelable {
    var id: String = ""
    var quantity = "1"
    var price = Random.nextInt(450, 500)
    var total_price = quantity.toInt() * price
}