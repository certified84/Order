package com.certified.order.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.certified.order.databinding.LayoutOrderBinding
import com.certified.order.model.Order
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MyOrdersRecyclerAdapter :
    FirestoreRecyclerAdapter<Order, MyOrdersRecyclerAdapter.ViewHolder>(
        options
    ) {

    companion object {
        private val options: FirestoreRecyclerOptions<Order> =
            FirestoreRecyclerOptions.Builder<Order>()
                .setQuery(Firebase.firestore.collection("orders"), Order::class.java).build()
    }

    inner class ViewHolder(val binding: LayoutOrderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(order: Order) {
            binding.order = order
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = LayoutOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Order) {
        holder.bind(model)
    }
}