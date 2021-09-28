package com.certified.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.certified.order.adapter.OtherBurgerAdapter
import com.certified.order.adapter.OtherBurgerAdapter.OnBurgerClickedListener
import com.certified.order.databinding.FragmentBurgersBinding
import com.certified.order.model.Burger
import com.certified.order.model.Review
import com.certified.order.view.DetailsFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import okhttp3.OkHttpClient
import okhttp3.Request

class BurgerFragment : Fragment() {

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
            Burger(0, "Krabby Patty", "Those who don't like Krabby patties haven't tasted it"),
            Burger(1, "Awesome Burger", "The taste is just awesome"),
            Burger(2, "King Burger", "Are you a king? Then this is for you"),
            Burger(3, "Vegan Burger", "Every vegan knows their stuff")
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

        val adapter = OtherBurgerAdapter(burgers)
        binding.recyclerViewBurgers.adapter = adapter

        adapter.setOnBurgerClickedListener(object : OnBurgerClickedListener {
            override fun onBookMarkClick(burger: Burger) {
                val fragmentManager = requireActivity().supportFragmentManager
                val completeOrderFragment = DetailsFragment("home", burger)
                val transaction = fragmentManager.beginTransaction()
                transaction
                    .add(android.R.id.content, completeOrderFragment)
                    .addToBackStack(null)
                    .commit()
            }
        })
    }

    fun load() {
        val client = OkHttpClient()

        val request = Request.Builder()
            .url("https://burgers1.p.rapidapi.com/burgers")
            .get()
            .addHeader("x-rapidapi-host", "burgers1.p.rapidapi.com")
            .addHeader("x-rapidapi-key", "34b9be6a3emshe7e3e482b50a80fp19ecadjsn77367a819a5b")
            .build()

        val response = client.newCall(request).execute()
    }
}