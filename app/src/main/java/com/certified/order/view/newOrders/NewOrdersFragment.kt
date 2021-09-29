package com.certified.order.view.newOrders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.certified.order.adapter.NewOrdersAdapter
import com.certified.order.databinding.FragmentNewOrdersBinding
import com.certified.order.model.Item
import com.certified.order.model.Order
import com.certified.order.view.OrderDetailsFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class NewOrdersFragment(private val orders: List<Order>) : Fragment() {

    private lateinit var binding: FragmentNewOrdersBinding
    private lateinit var auth: FirebaseAuth

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

        val burgers = listOf(
            Item(0, "Krabby Patty", "Those who don't like Krabby patties haven't tasted it"),
            Item(1, "Awesome Item", "The taste is just awesome"),
            Item(2, "King Item", "Are you a king? Then this is for you"),
            Item(3, "Vegan Item", "Every vegan knows their stuff")
        )
//        val viewModelFactory = OrderViewModelFactory(burgers)
//        val viewModel: OtherBurgerViewModel by lazy {
//            ViewModelProvider(this, viewModelFactory).get(OtherBurgerViewModel::class.java)
//        }
//
//        viewModel.showProgressBar.observe(viewLifecycleOwner) {
//            if (it)
//                binding.progressBar.visibility = View.VISIBLE
//            else
//                binding.progressBar.visibility = View.GONE
//        }
//
//        binding.viewModel = viewModel
//        binding.lifecycleOwner = this
//        binding.recyclerViewBurgers.layoutManager = LinearLayoutManager(requireContext())

        val adapter = NewOrdersAdapter(orders)
        binding.recyclerViewNewOrders.adapter = adapter

        adapter.setOnOrderClickedListener(object : NewOrdersAdapter.OnOrderClickedListener {
            override fun onOrderClick(order: Order) {
                val fragmentManager = requireActivity().supportFragmentManager
                val completeOrderFragment = OrderDetailsFragment(order)
                val transaction = fragmentManager.beginTransaction()
                transaction
                    .add(android.R.id.content, completeOrderFragment)
                    .addToBackStack(null)
                    .commit()
            }
        })
    }
}