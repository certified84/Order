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
import com.certified.order.adapter.ItemAdapter
import com.certified.order.databinding.FragmentItemsBinding
import com.certified.order.model.Item
import com.certified.order.view.details.ItemDetailsFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ItemFragment : Fragment() {

    companion object {
        lateinit var adapter: ItemAdapter
        lateinit var items: List<Item>
    }

    private lateinit var binding: FragmentItemsBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentItemsBinding.inflate(layoutInflater)
        auth = Firebase.auth
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        TODO: Convert to Json
        items = listOf(
            Item(
                "Krabby Patty",
                "Those who don't like Krabby patties haven't tasted it",
                "burger"
            ),
            Item(
                "Krabby Shawarma",
                "You remember how awesome those Patties where? This Shawarma isn't slacking either",
                "shawarma"
            ),
            Item(
                "Krabby Chicken and chips",
                "Straight from Bikini bottom made by the awesome chef Sponge Bob himself",
                "chicken and chips"
            ),
            Item(
                "Beef Pizza",
                "You love beef? You came to the right place.",
                "pizza"
            ),
            Item(
                "Awesome Shawarma", "As an awesome being, you should check this out",
                "shawarma"
            ),
            Item(
                "Awesome Chicken and chips",
                "The taste is just aweeeeeeeesome. Why not try some chief",
                "chicken and chips"
            ),
            Item(
                "Awesome Pizza",
                "Macaroni and cheese saw this and retired. Think I'm lying? See for yourself",
                "pizza"
            ),
            Item(
                "Awesome Burger", "The taste is just as awesome as the name",
                "burger"
            ),
            Item(
                "King Chicken and chips", "Are you a king? Then this is for you",
                "chicken and chips"
            ),
            Item(
                "King Pizza",
                "Are you a king? Then this king pepperoni pizza is for you",
                "pizza"
            ),
            Item(
                "King Burger", "A true king knows their stuff when they see one",
                "burger"
            ),
            Item(
                "King Shawarma",
                "Women are Kings too. As a King, you deserve the best",
                "shawarma"
            ),
            Item(
                "Vegan Burger", "Every vegan knows their stuff",
                "burger"
            ),
            Item(
                "Vegan Shawarma", "Every vegan knows their stuff",
                "shawarma"
            ),
            Item(
                "Vegan Pizza", "Every vegan knows their stuff",
                "pizza"
            ),
            Item(
                "Vegan Chicken and chips", "Every vegan knows their stuff",
                "chicken and chips"
            ),
            Item(
                "Chicken Shawarma",
                "I bet you love eating chicken. If you do, then this is for you",
                "shawarma"
            ),
            Item(
                "Plain Chicken and chips",
                "I bet you love eating chicken. If you do, then this is for you",
                "chicken and chips"
            ),
            Item(
                "Chicken Pizza",
                "I bet you love eating chicken. If you do, then this is for you",
                "pizza"
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
        binding.recyclerViewItems.layoutManager = LinearLayoutManager(requireContext())

        adapter = ItemAdapter(items)
        binding.recyclerViewItems.adapter = adapter

        adapter.setOnItemClickedListener(object : ItemAdapter.OnItemClickedListener {
            override fun onItemClick(item: Item) {
                val fragmentManager = requireActivity().supportFragmentManager
                val completeOrderFragment =
                    ItemDetailsFragment("home", item, auth.currentUser!!.uid)
                val transaction = fragmentManager.beginTransaction()
                transaction
                    .add(android.R.id.content, completeOrderFragment)
                    .addToBackStack(null)
                    .commit()
            }
        })
    }
}