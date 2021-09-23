package com.certified.order.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.certified.order.R
import com.certified.order.databinding.FragmentReviewBinding

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
}