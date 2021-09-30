package com.certified.order.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.certified.order.ReviewViewModel
import com.certified.order.ReviewViewModelFactory
import com.certified.order.adapter.ReviewAdapter
import com.certified.order.databinding.FragmentReviewBinding
import com.certified.order.model.Review

class ReviewFragment : Fragment() {

    private lateinit var binding: FragmentReviewBinding

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
            Review(0, "Samson Achiaga", "I made the app so I'll definitely give a 5 star", 5f),
            Review(1, "Daniel Achiaga", "My brother made it so you can trust them.", 4f),
            Review(2, "Y3k & Bbno$", "Whatever man I'm just typing shit", 3f),
            Review(3, "Fuck off", "I said what I said. What you gon do?", 2f),
            Review(4, "Mr. Nobody", "Nigga y'all dumb asf", 1f)
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
        binding.recyclerViewReviews.layoutManager = LinearLayoutManager(requireContext())

        val adapter = ReviewAdapter(reviews)
        binding.recyclerViewReviews.adapter = adapter
    }
}