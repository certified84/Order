package com.certified.order.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import com.certified.order.R
import com.certified.order.databinding.FragmentSignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SignupFragment : Fragment() {

    private lateinit var navController: NavController
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentSignupBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSignupBinding.inflate(layoutInflater)
        auth = Firebase.auth

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)
        val currentUser = auth.currentUser

        binding.apply {
            btnSignup.setOnClickListener {

                val name = etName.text.toString().trim()
                val email = etEmail.text.toString().trim()
                val password = etPassword.text.toString().trim()
                val accountType = spinnerAccountType.selectedItem.toString()

                if (currentUser == null) {
                    if (email.isNotEmpty() && password.isNotEmpty() && name.isNotEmpty()) {
                        if (accountType != "Select account type") {
                            progressBar.visibility = View.VISIBLE

                            auth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(requireActivity()) { task ->
                                    if (task.isSuccessful) {

                                        progressBar.visibility = View.GONE
                                        val user = auth.currentUser

                                        if (accountType != "Dispatcher")
                                            user?.sendEmailVerification()
                                        else
                                            mailAdmin()

                                        val newUser =
                                            com.certified.order.model.User(
                                                name,
                                                "",
                                                "",
                                            )
                                        newUser.id = user!!.uid
                                        newUser.email = user.email.toString()

                                        val db = Firebase.firestore
                                        val userRef =
                                            db.collection("users").document(user.uid)
                                        userRef.set(newUser).addOnCompleteListener {
                                            if (it.isSuccessful) {
                                                val profileChangeRequest =
                                                    UserProfileChangeRequest.Builder()
                                                        .setDisplayName(newUser.name)
                                                        .setPhotoUri(newUser.profileImage)
                                                        .build()
                                                user.updateProfile(profileChangeRequest)

                                                Firebase.auth.signOut()

                                                val navOptions = NavOptions.Builder()
                                                    .setPopUpTo(R.id.onboardingFragment, true).build()
                                                navController.navigate(
                                                    R.id.loginFragment,
                                                    null,
                                                    navOptions
                                                )
                                            } else
                                                Toast.makeText(
                                                    requireContext(),
                                                    "An error occurred: ${it.exception}",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                        }

                                        Toast.makeText(
                                            requireContext(),
                                            "Success. Check email for verification link",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                    } else {
                                        Toast.makeText(
                                            requireContext(),
                                            "Authentication failed ${task.exception}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Please select an account type",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "All fields are required",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }

                tvLogin.setOnClickListener {
                    val navOptions = NavOptions.Builder()
                        .setPopUpTo(R.id.signupFragment, true).build()
                    navController.navigate(R.id.loginFragment, null, navOptions)
                }
        }
    }

    private fun mailAdmin() {
//        TODO("Not yet implemented")
    }
}