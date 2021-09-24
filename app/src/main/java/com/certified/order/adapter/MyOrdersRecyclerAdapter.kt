package com.certified.order.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.certified.order.R
import com.certified.order.model.Order
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class MyOrdersRecyclerAdapter :
    FirestoreRecyclerAdapter<Order, MyOrdersRecyclerAdapter.ViewHolder>(
    options
) {

    companion object {
        private val options: FirestoreRecyclerOptions<Order> =
            FirestoreRecyclerOptions.Builder<Order>().build()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

//        val noteContent: TextView = itemView.findViewById(R.id.tv_note_content)
//        val noteTitle: TextView = itemView.findViewById(R.id.tv_note_title)
//        val ivBookMark: ImageView = itemView.findViewById(R.id.iv_bookmark)

//        init {
//            itemView.setOnClickListener {
//                val position = absoluteAdapterPosition
//                if (position != RecyclerView.NO_POSITION) {
//                    listener.onBookMarkClick(getItem(position))
//                }
//            }
//        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Order) {

    }
}