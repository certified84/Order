package com.certified.order.view.orders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.certified.order.R
import com.certified.order.adapter.OrdersRecyclerAdapter
import com.certified.order.databinding.FragmentPendingOrdersBinding
import com.certified.order.model.Order
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.getField
import com.google.firebase.ktx.Firebase

class PendingOrdersFragment : Fragment() {

    //    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentPendingOrdersBinding
    private lateinit var adapter: OrdersRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentPendingOrdersBinding.inflate(layoutInflater)

//        auth = Firebase.auth

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val orders = ArrayList<Order>()
        val query =
            Firebase.firestore.collection("orders")
                .whereEqualTo("status", "Pending")
        query.get().addOnSuccessListener {
            for (querySnapShot in it) {

                val order = Order(
                    querySnapShot.getString("receiver_name")!!,
                    querySnapShot.getField("receiver_photourl"),
                    querySnapShot.getString("receiver_phone_no")!!
                )
                order.id = querySnapShot.getString("id")!!
                order.receiver_id = querySnapShot.getString("receiver_id")!!
                order.deliveryTime = querySnapShot.getString("deliveryTime")!!
                order.isDelivered = querySnapShot.getBoolean("delivered")!!
                order.latitude = querySnapShot.getString("latitude")
                order.longitude = querySnapShot.getString("longitude")
                order.isRated = querySnapShot.getBoolean("rated")!!
                order.dispatcher_name = querySnapShot.getString("dispatcher_name")!!
                order.dispatcher_phone_no = querySnapShot.getString("dispatcher_phone_no")!!
                order.status = querySnapShot.getString("status")!!

                orders.add(order)
            }
        }
        val options =
            FirestoreRecyclerOptions.Builder<Order>().setQuery(query, Order::class.java).build()
        adapter = OrdersRecyclerAdapter(options)

        val viewModelFactory = OrderViewModelFactory(orders)
        val viewModel: OrderViewModel by lazy {
            ViewModelProvider(viewModelStore, viewModelFactory).get(OrderViewModel::class.java)
        }

        viewModel.showProgressBar.observe(viewLifecycleOwner) {
            if (it)
                binding.progressBar.visibility = View.VISIBLE
            else
                binding.progressBar.visibility = View.GONE
        }

        viewModel.showEmptyOrderDesign.observe(viewLifecycleOwner) {
            binding.apply {
                if (it) {
                    groupEmptyCart.visibility = View.VISIBLE
                    recyclerViewPendingOrders.visibility = View.GONE
                } else {
                    groupEmptyCart.visibility = View.GONE
                    recyclerViewPendingOrders.visibility = View.VISIBLE
                }
            }
        }

        binding.lifecycleOwner = viewLifecycleOwner

        binding.apply {
            recyclerViewPendingOrders.adapter = adapter
            recyclerViewPendingOrders.layoutManager = LinearLayoutManager(requireContext())

//            TODO: This shit might crash
            val navOptions = NavOptions.Builder().setPopUpTo(R.id.cartFragment, true).build()
            btnPlaceOrder.setOnClickListener {
                findNavController().navigate(R.id.cartFragment, null, navOptions)
            }

            adapter.setOnOrderClickedListener(object :
                OrdersRecyclerAdapter.OnOrderClickedListener {
                override fun onOrderClick(order: Order) {
//                    TODO: Show order status
                }
            })
        }
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }
}