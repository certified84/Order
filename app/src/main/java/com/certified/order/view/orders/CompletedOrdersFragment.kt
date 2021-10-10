package com.certified.order.view.orders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.certified.order.adapter.CompletedOrdersAdapter
import com.certified.order.databinding.FragmentCompletedOrdersBinding
import com.certified.order.model.Order
import com.certified.order.view.OrderDetailsFragment
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class CompletedOrdersFragment : Fragment() {

    private lateinit var binding: FragmentCompletedOrdersBinding
    private lateinit var adapter: CompletedOrdersAdapter
    private lateinit var navController: NavController
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentCompletedOrdersBinding.inflate(layoutInflater)

        auth = Firebase.auth

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val orders = ArrayList<Order>()
        val query =
            Firebase.firestore.collection("dispatcher_orders").document(auth.currentUser!!.uid)
                .collection("my_completed_orders")
                .whereEqualTo("status", "Delivered")
        query.get().addOnSuccessListener {
            for (querySnapShot in it) {

                val order = querySnapShot.toObject(Order::class.java)
                order.id = querySnapShot.id

                orders.add(order)
            }
        }
        val options =
            FirestoreRecyclerOptions.Builder<Order>().setQuery(query, Order::class.java).build()
        adapter = CompletedOrdersAdapter(options)

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
                    recyclerViewCompletedOrders.visibility = View.GONE
                } else {
                    groupEmptyCart.visibility = View.GONE
                    recyclerViewCompletedOrders.visibility = View.VISIBLE
                }
            }
        }

        binding.lifecycleOwner = viewLifecycleOwner

        binding.apply {
            recyclerViewCompletedOrders.adapter = adapter
            recyclerViewCompletedOrders.layoutManager = LinearLayoutManager(requireContext())

            adapter.setOnOrderClickedListener(object :
                CompletedOrdersAdapter.OnOrderClickedListener {
                override fun onOrderClick(order: Order) {
//                    TODO: Show order details
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

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }
}