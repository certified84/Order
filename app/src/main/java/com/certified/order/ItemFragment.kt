package com.certified.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.certified.order.adapter.ItemAdapter
import com.certified.order.adapter.ItemAdapter.*
import com.certified.order.adapter.ItemAdapter.OnItemClickedListener
import com.certified.order.databinding.FragmentBurgersBinding
import com.certified.order.model.Item
import com.certified.order.view.DetailsFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import okhttp3.OkHttpClient
import okhttp3.Request

class ItemFragment : Fragment() {

    private lateinit var binding: FragmentBurgersBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentBurgersBinding.inflate(layoutInflater)

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
        val apiService = BurgerApi.apiService
        val viewModelFactory = OrderViewModelFactory(burgers)
        val viewModel: OtherBurgerViewModel by lazy {
            ViewModelProvider(this, viewModelFactory).get(OtherBurgerViewModel::class.java)
        }

        viewModel.showProgressBar.observe(viewLifecycleOwner) {
            if (it)
                binding.progressBar.visibility = View.VISIBLE
            else
                binding.progressBar.visibility = View.GONE
        }

        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.recyclerViewBurgers.layoutManager = LinearLayoutManager(requireContext())

        val adapter = ItemAdapter(burgers)
        binding.recyclerViewBurgers.adapter = adapter

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