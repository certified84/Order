package com.certified.order.view.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.certified.order.ItemViewModel
import com.certified.order.ItemViewModelFactory
import com.certified.order.adapter.CartAdapter
import com.certified.order.databinding.FragmentCartBinding
import com.certified.order.model.Item
import com.certified.order.view.CompleteOrderFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class CartFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentCartBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentCartBinding.inflate(layoutInflater)

        auth = Firebase.auth

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        TODO: Get the cart items from firebase
        val burgers = listOf(
            Item(0, "Krabby Patty", "Those who don't like Krabby patties haven't tasted it"),
            Item(1, "Awesome Item", "The taste is just awesome"),
            Item(2, "King Item", "Are you a king? Then this is for you"),
            Item(3, "Vegan Item", "Every vegan knows their stuff")
        )
        val viewModelFactory = ItemViewModelFactory(burgers)
        val viewModel: ItemViewModel by lazy {
            ViewModelProvider(this, viewModelFactory).get(ItemViewModel::class.java)
        }

        viewModel.showProgressBar.observe(viewLifecycleOwner) {
            if (it)
                binding.progressBar.visibility = View.VISIBLE
            else
                binding.progressBar.visibility = View.GONE
        }

        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.recyclerViewItems.layoutManager = LinearLayoutManager(requireContext())

        val adapter = CartAdapter(burgers)
        binding.recyclerViewItems.adapter = adapter

        binding.apply {
            btnCompleteOrder.setOnClickListener {
                showCompleteOrderDialog(burgers)
            }
        }
    }

    private fun showCompleteOrderDialog(burgers: List<Item>) {
        val fragmentManager = requireActivity().supportFragmentManager
        val completeOrderFragment = CompleteOrderFragment(burgers)
        val transaction = fragmentManager.beginTransaction()
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        transaction
            .add(android.R.id.content, completeOrderFragment)
            .addToBackStack(null)
            .commit()
    }
}