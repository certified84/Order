package com.certified.order.view.cart

import android.net.Uri
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
import com.certified.order.adapter.OtherCartAdapter
import com.certified.order.databinding.FragmentCartBinding
import com.certified.order.model.Item
import com.certified.order.view.CompleteOrderFragment
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class CartFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentCartBinding
    private lateinit var adapter: OtherCartAdapter

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
        val items = listOf(
            Item(
                "Krabby Patty",
                "Those who don't like Krabby patties haven't tasted it",
                "burger"
            ),
            Item(
                "Awesome Chicken and chips",
                "The taste is just awesome","chicken and chips"
            ),
            Item(
                "King Pizza",
                "Are you a king? Then this is for you",
                "pizza"
            ),
            Item(
                "Vegan Shawarma",
                "Every vegan knows their stuff",
                "shawarma"
            )
        )
        val viewModelFactory = ItemViewModelFactory(items)
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

        val cartItems =
            Firebase.firestore.collection("cart").document(Firebase.auth.currentUser!!.uid).collection("my_cart_items")
        val query = cartItems.orderBy("id")
        val options = FirestoreRecyclerOptions.Builder<Item>().setQuery(query, Item::class.java).build()
        adapter = OtherCartAdapter(options)
        binding.recyclerViewItems.adapter = adapter
        binding.recyclerViewItems.layoutManager = LinearLayoutManager(requireContext())

        binding.apply {
            btnCompleteOrder.setOnClickListener {
                showCompleteOrderDialog(items)
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

    private fun showCompleteOrderDialog(items: List<Item>) {
        val fragmentManager = requireActivity().supportFragmentManager
        val completeOrderFragment = CompleteOrderFragment(items)
        val transaction = fragmentManager.beginTransaction()
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        transaction
            .add(android.R.id.content, completeOrderFragment)
            .addToBackStack(null)
            .commit()
    }
}