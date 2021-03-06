package com.certified.order.view.orders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.certified.order.R
import com.certified.order.adapter.OrdersRecyclerAdapter
import com.certified.order.databinding.FragmentNewOrdersBinding
import com.certified.order.model.Item
import com.certified.order.model.Order
import com.certified.order.view.OrderDetailsFragment
import com.certified.order.view.SignupFragment
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.getField
import com.google.firebase.ktx.Firebase

class NewOrdersFragment : Fragment() {

    private lateinit var binding: FragmentNewOrdersBinding
    private lateinit var adapter: OrdersRecyclerAdapter
    private lateinit var auth: FirebaseAuth
    private lateinit var viewModel: OrderViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentNewOrdersBinding.inflate(layoutInflater)

        auth = Firebase.auth

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val orders = ArrayList<Order>()
        val query =
            Firebase.firestore.collection("all_orders")
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
        viewModel =
            ViewModelProvider(this, viewModelFactory).get(OrderViewModel::class.java)

        viewModel.showEmptyOrderDesign.observe(viewLifecycleOwner) {
            binding.apply {
                if (it) {
                    groupEmptyCart.visibility = View.VISIBLE
                    recyclerViewNewOrders.visibility = View.GONE
                } else {
                    groupEmptyCart.visibility = View.GONE
                    recyclerViewNewOrders.visibility = View.VISIBLE
                }
            }
        }

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.apply {
            recyclerViewNewOrders.adapter = adapter
            recyclerViewNewOrders.layoutManager = LinearLayoutManager(requireContext())

            adapter.setOnOrderClickedListener(object :
                OrdersRecyclerAdapter.OnOrderClickedListener {
                override fun onOrderClick(order: Order) {
                    val fragmentManager = requireActivity().supportFragmentManager
                    val orderDetailsFragment = OrderDetailsFragment(order)
                    val transaction = fragmentManager.beginTransaction()
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    transaction
                        .add(android.R.id.content, orderDetailsFragment)
                        .addToBackStack(null)
                        .commit()
                }
            })
        }
    }

    override fun onResume() {
        super.onResume()

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
                    recyclerViewNewOrders.visibility = View.GONE
                } else {
                    groupEmptyCart.visibility = View.GONE
                    recyclerViewNewOrders.visibility = View.VISIBLE
                }
            }
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