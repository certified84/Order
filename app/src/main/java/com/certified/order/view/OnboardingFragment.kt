package com.certified.order.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import com.certified.order.R
import com.certified.order.databinding.FragmentOnboardingBinding
import com.google.firebase.auth.FirebaseAuth

class OnboardingFragment : Fragment() {

    private lateinit var binding: FragmentOnboardingBinding
    private lateinit var navController: NavController
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOnboardingBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)
        val currentUser = auth.currentUser

        if (currentUser != null) {
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.onboardingFragment, true).build()
            navController.navigate(R.id.homeFragment, null, navOptions)
        }
    }
}