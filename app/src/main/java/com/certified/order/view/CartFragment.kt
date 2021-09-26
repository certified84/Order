package com.certified.order.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentTransaction
import com.certified.order.R
import com.certified.order.databinding.FragmentCartBinding
import com.certified.order.model.Burger
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class CartFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentCartBinding

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
        binding.apply {
            btnCompleteOrder.setOnClickListener {
                showCompleteOrderDialog()
            }
        }
    }

    private fun showCompleteOrderDialog() {
        val burgers = listOf(
            Burger(0, "Krabby Patty", "Those who don't like Krabby patties haven't tasted it"),
            Burger(1, "Awesome Burger", "The taste is just awesome"),
            Burger(2, "King Burger", "Are you a king? Then this is for you"),
            Burger(3, "Vegan Burger", "Every vegan knows their stuff")
        )
        val fragmentManager = requireActivity().supportFragmentManager
        val completeOrderFragment = CompleteOrderFragment(burgers)
        // The device is smaller, so show the fragment fullscreen
        val transaction = fragmentManager.beginTransaction()
        // For a little polish, specify a transition animation
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        // To make it fullscreen, use the 'content' root view as the container
        // for the fragment, which is always the root view for the activity
        transaction
            .add(android.R.id.content, completeOrderFragment)
            .addToBackStack(null)
            .commit()
    }
}