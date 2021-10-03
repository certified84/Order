package com.certified.order.view.items

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.certified.order.ItemViewModel
import com.certified.order.ItemViewModelFactory
import com.certified.order.adapter.ItemAdapter
import com.certified.order.adapter.ItemAdapter.OnItemClickedListener
import com.certified.order.databinding.FragmentItemsBinding
import com.certified.order.model.Item
import com.certified.order.view.DetailsFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ShawarmaFragment : Fragment() {

    private lateinit var binding: FragmentItemsBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentItemsBinding.inflate(layoutInflater)

        auth = Firebase.auth

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val shawarmas = listOf(
            Item(
                "Krabby Shawarma",
                "Those who don't like Krabby patties haven't tasted it",
                "shawarma"
            ),
            Item(
                "Awesome Shawarma", "The taste is just awesome",
                "shawarma"
            ),
            Item(
                "King Shawarma",
                "Are you a king? Then this is for you",
                "shawarma"
            ),
            Item(
                "Vegan Shawarma", "Every vegan knows their stuff",
                "shawarma"
            ),
            Item(
                "Chicken Shawarma",
                "I bet you love eating chicken. If you do, then this is for you",
                "shawarma"
            )
        )
        val viewModelFactory = ItemViewModelFactory(shawarmas)
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

        val adapter = ItemAdapter(shawarmas, "Shawarma")
        binding.recyclerViewItems.adapter = adapter

        adapter.setOnItemClickedListener(object : OnItemClickedListener {
            override fun onItemClick(item: Item) {
                val fragmentManager = requireActivity().supportFragmentManager
                val completeOrderFragment = DetailsFragment("home", item, auth.currentUser!!.uid)
                val transaction = fragmentManager.beginTransaction()
                transaction
                    .add(android.R.id.content, completeOrderFragment)
                    .addToBackStack(null)
                    .commit()
            }
        })
    }
}