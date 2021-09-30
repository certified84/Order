package com.certified.order.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.certified.order.ItemViewModel
import com.certified.order.ItemViewModelFactory
import com.certified.order.R
import com.certified.order.adapter.ItemAdapter
import com.certified.order.adapter.ItemAdapter.OnItemClickedListener
import com.certified.order.databinding.FragmentItemsBinding
import com.certified.order.model.Item
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class PizzaFragment : Fragment() {

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

//        TODO: change the burger picture to pizza
        val pizzas = listOf(
            Item(
                0,
                "Krabby Patty",
                "Those who don't like Krabby patties haven't tasted it",
                R.drawable.burger_image_3
            ),
            Item(1, "Awesome Pizza", "The taste is just awesome", R.drawable.burger_image_3),
            Item(
                2,
                "King Pizza",
                "Are you a king? Then this is for you",
                R.drawable.burger_image_3
            ),
            Item(3, "Vegan Pizza", "Every vegan knows their stuff", R.drawable.burger_image_3),
            Item(
                4,
                "Chicken Pizza",
                "I bet you love eating chicken. If you do, then this is for you",
                R.drawable.burger_image_3
            )
        )
        val viewModelFactory = ItemViewModelFactory(pizzas)
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

        val adapter = ItemAdapter(pizzas)
        binding.recyclerViewItems.adapter = adapter

        adapter.setOnItemClickedListener(object : OnItemClickedListener {
            override fun onItemClick(item: Item) {
                val fragmentManager = requireActivity().supportFragmentManager
                val completeOrderFragment = DetailsFragment("home", item)
                val transaction = fragmentManager.beginTransaction()
                transaction
                    .add(android.R.id.content, completeOrderFragment)
                    .addToBackStack(null)
                    .commit()
            }
        })
    }
}