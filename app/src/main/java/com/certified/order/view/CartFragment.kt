package com.certified.order.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.certified.order.ItemViewModel
import com.certified.order.ItemViewModelFactory
import com.certified.order.R
import com.certified.order.adapter.CartAdapter
import com.certified.order.databinding.FragmentCartBinding
import com.certified.order.model.Item
import com.certified.order.model.Order
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.getField
import com.google.firebase.ktx.Firebase

class CartFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentCartBinding
    private lateinit var adapter: CartAdapter

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

        val items = ArrayList<Item>()
        val query =
            Firebase.firestore.collection("cart").document(Firebase.auth.currentUser!!.uid)
                .collection("my_cart_items").orderBy("id")
        query.get().addOnSuccessListener {
            for (querySnapshot in it) {
                val item = querySnapshot.toObject(Item::class.java)
                item.id = querySnapshot.id
                items.add(item)
            }
        }
        val options =
            FirestoreRecyclerOptions.Builder<Item>().setQuery(query, Item::class.java).build()
        adapter = CartAdapter(options)

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

        viewModel.showEmptyCartDesign.observe(viewLifecycleOwner) {
            binding.apply {
                if (it) {
                    groupEmptyCart.visibility = View.VISIBLE
                    groupCartItems.visibility = View.GONE
                } else {
                    groupEmptyCart.visibility = View.GONE
                    groupCartItems.visibility = View.VISIBLE
                }
            }
        }

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.apply {
            recyclerViewItems.adapter = adapter
            recyclerViewItems.layoutManager = LinearLayoutManager(requireContext())
            val navOptions = NavOptions.Builder().setPopUpTo(R.id.homeFragment, true).build()
            btnAddToCart.setOnClickListener {
                findNavController().navigate(R.id.homeFragment, null, navOptions)
            }
            btnCheckout.setOnClickListener {
                showConfirmOrderDialog(items)
            }

            ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    adapter.deleteItem(viewHolder.adapterPosition)
                }
            }).attachToRecyclerView(recyclerViewItems)
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

    private fun showConfirmOrderDialog(items: List<Item>) {
        val fragmentManager = requireActivity().supportFragmentManager
        val completeOrderFragment = ConfirmOrderFragment(items, "Cart")
        val transaction = fragmentManager.beginTransaction()
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .add(android.R.id.content, completeOrderFragment)
            .addToBackStack(null)
            .commit()
    }
}