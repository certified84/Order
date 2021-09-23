package com.certified.order.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import com.certified.order.R
import com.certified.order.databinding.FragmentOnboardingBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class OnboardingFragment : Fragment() {

    private lateinit var binding: FragmentOnboardingBinding
    private lateinit var navController: NavController
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentOnboardingBinding.inflate(layoutInflater)

        auth = Firebase.auth

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)
        val currentUser = auth.currentUser

        if (currentUser != null)
            queryDatabase(currentUser)

        binding.btnGetStarted.setOnClickListener {
//            val navOptions = NavOptions.Builder()
//                .setPopUpTo(R.id.onboardingFragment, true).build()
//                navController.navigate(R.id.signupFragment, null, navOptions)
            showSignupDialog()
        }
    }

    private fun showSignupDialog() {
        val isLargeLayout = resources.getBoolean(R.bool.large_layout)
        val fragmentManager = requireActivity().supportFragmentManager
        val signupFragment = SignupFragment()
            // The device is smaller, so show the fragment fullscreen
            val transaction = fragmentManager.beginTransaction()
            // For a little polish, specify a transition animation
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            // To make it fullscreen, use the 'content' root view as the container
            // for the fragment, which is always the root view for the activity
            transaction
                .add(android.R.id.content, signupFragment)
                .addToBackStack(null)
                .commit()
    }

    private fun queryDatabase(user: FirebaseUser) {
        val db = Firebase.firestore
        val userRef =
            db.collection("account_type").document(user.uid)
        userRef.get().addOnSuccessListener {
            if (it.exists()) {

                val accountType = it.getString("account_type")
                val isApproved = it.getBoolean("_approved")
                val navOptions = NavOptions.Builder()
                    .setPopUpTo(R.id.onboardingFragment, true).build()

                if (accountType == "Dispatcher" && isApproved!!)
                    Toast.makeText(requireContext(), "You have been approved", Toast.LENGTH_LONG).show()
                else
                    navController.navigate(R.id.homeFragment, null, navOptions)
            }
        }
    }
}