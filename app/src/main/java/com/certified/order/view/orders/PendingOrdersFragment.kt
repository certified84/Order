package com.certified.order.view.orders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.certified.order.R
import com.certified.order.adapter.OrdersRecyclerAdapter
import com.certified.order.databinding.FragmentPendingOrdersBinding
import com.certified.order.model.Order
import com.certified.order.view.OrderTrackingFragment
import com.certified.order.view.SignupFragment
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
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
            Firebase.firestore.collection("user_orders").document(Firebase.auth.currentUser!!.uid)
                .collection("my_orders")
                .whereEqualTo("status", "Pending")
        query.get().addOnSuccessListener {
            for (querySnapShot in it) {

                val order = querySnapShot.toObject(Order::class.java)
                order.id = querySnapShot.id
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
//                    TODO: Show order tracking
                    if (order.dispatcher_name.isNotEmpty()) {
                        val fragmentManager = requireActivity().supportFragmentManager
                        val orderTrackingFragment = OrderTrackingFragment(order)
                        val transaction = fragmentManager.beginTransaction()
                        transaction
                            .add(android.R.id.content, orderTrackingFragment)
//                            .addToBackStack(null)
                            .commit()
                    } else Toast.makeText(
                        requireContext(),
                        "Order hasn't been picked up by a dispatcher. When they do, you'll be able to track it",
                        Toast.LENGTH_LONG
                    ).show()
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