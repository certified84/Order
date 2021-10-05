package com.certified.order.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.certified.order.databinding.FragmentNewReviewBinding
import com.certified.order.model.Order
import com.certified.order.model.Review
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class NewReviewFragment(val order: Order) : DialogFragment() {

    private lateinit var binding: FragmentNewReviewBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentNewReviewBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            btnSubmitReview.setOnClickListener {
                if (ratingBar.rating != 0f && etReview.text.toString().isNotEmpty()) {
                    progressBar.visibility = View.VISIBLE
                    val review = Review(
                        Firebase.auth.currentUser?.displayName!!,
                        etReview.text.toString(),
                        ratingBar.rating.toInt()
                    )
                    val reviewRef = Firebase.firestore.collection("reviews").document()
                    review.id = reviewRef.id
                    reviewRef.set(review).addOnCompleteListener {
                        if (it.isSuccessful) {
                            Firebase.firestore.collection("orders").document(order.id)
                                .update("rated", true).addOnSuccessListener {
//                            TODO: Change the isRated field of the current order to true
                                    progressBar.visibility = View.GONE
                                    dismiss()
                                }
                        }
                    }
                } else
                    Toast.makeText(requireContext(), "Please leave a review", Toast.LENGTH_LONG)
                        .show()
            }
            btnLater.setOnClickListener { dismiss() }
        }
    }
}