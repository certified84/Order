package com.certified.order.model

import android.net.Uri

data class Item(val name: String, val price: Double, val images: List<Uri>?, val desc: String, val quantity: String)