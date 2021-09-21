package com.certified.order.model

import android.net.Uri

data class Item(val name: String, val price: Double, val image: Uri?, val desc: String)