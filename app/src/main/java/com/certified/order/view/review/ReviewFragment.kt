package com.certified.order.view.review

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.certified.order.adapter.ReviewAdapter
import com.certified.order.databinding.FragmentReviewBinding
import com.certified.order.model.Item
import com.certified.order.model.Review
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ReviewFragment : Fragment() {

    private lateinit var binding: FragmentReviewBinding
    private lateinit var adapter: ReviewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReviewBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val reviews = listOf(
            Review("Samson Achiaga", "I made the app so I'll definitely give a 5 star", Firebase.auth.currentUser!!.photoUrl, 5),
            Review("Daniel Achiaga", "My brother made it so you can trust them.", Firebase.auth.currentUser!!.photoUrl, 4),
            Review("Y3k & Bbno$", "Whatever man I'm just typing shit", Firebase.auth.currentUser!!.photoUrl, 3),
            Review("Fuck off", "I said what I said. What you gon do?", Firebase.auth.currentUser!!.photoUrl, 2),
            Review("Mr. Nobody", "Nigga y'all dumb asf", Firebase.auth.currentUser!!.photoUrl, 1)
        )
        val viewModelFactory = ReviewViewModelFactory(reviews)
        val viewModel: ReviewViewModel by lazy {
            ViewModelProvider(this, viewModelFactory).get(ReviewViewModel::class.java)
        }

        viewModel.showProgressBar.observe(viewLifecycleOwner) {
            if (it)
                binding.progressBar.visibility = View.VISIBLE
            else
                binding.progressBar.visibility = View.GONE
        }

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        val query = Firebase.firestore.collection("reviews").orderBy("id")
        val options =
            FirestoreRecyclerOptions.Builder<Review>().setQuery(query, Review::class.java).build()

        adapter = ReviewAdapter(options)
        binding.recyclerViewReviews.adapter = adapter
        binding.recyclerViewReviews.layoutManager = LinearLayoutManager(requireContext())
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