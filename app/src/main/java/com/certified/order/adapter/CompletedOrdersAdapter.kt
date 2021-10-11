package com.certified.order.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.certified.order.databinding.LayoutOrderBinding
import com.certified.order.model.Order
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class CompletedOrdersAdapter(options: FirestoreRecyclerOptions<Order>) :
    FirestoreRecyclerAdapter<Order, CompletedOrdersAdapter.ViewHolder>(
        options
    ) {

    private lateinit var listener: OnOrderClickedListener

    inner class ViewHolder(val binding: LayoutOrderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(order: Order) {
            binding.order = order
        }

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onOrderClick(getItem(position))
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = LayoutOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Order) {
        holder.bind(model)
    }

    interface OnOrderClickedListener {
        fun onOrderClick(order: Order)
    }

    fun setOnOrderClickedListener(listener: OnOrderClickedListener) {
        this.listener = listener
    }
}