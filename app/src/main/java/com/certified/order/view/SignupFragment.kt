package com.certified.order.view

import android.annotation.SuppressLint
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
import com.certified.order.util.Config
import com.certified.order.util.Mailer
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

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
                val phone = etPhone.text.toString().trim()
                val email = etEmail.text.toString().trim()
                val password = etPassword.text.toString().trim()
                val accountType = spinnerAccountType.selectedItem.toString()

                if (currentUser == null) {
                    if (email.isNotEmpty() && password.isNotEmpty() && name.isNotEmpty() && phone.isNotEmpty()) {
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
                                            mailAdmin(user!!)

                                        val newUser =
                                            com.certified.order.model.User(
                                                name,
                                                phone,
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
                                                    .setPopUpTo(R.id.onboardingFragment, true)
                                                    .build()
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

    @SuppressLint("CheckResult")
    private fun mailAdmin(user: FirebaseUser) {
        val email = Config.EMAIL
        val subject = "New dispatcher registration"
        val message = "A new account has been registered as a dispatcher. Find details below\n" +
                "Name: ${user.displayName}\n" +
                "Email: ${user.email}"

        Mailer.sendMail(email, subject, message).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { showDialog() }
    }

    private fun showDialog() {
        val alertDialogBuilder = MaterialAlertDialogBuilder(requireContext())
        alertDialogBuilder.setTitle("Application sent")
        alertDialogBuilder.setMessage(
            "Your application for dispatcher account has been sent. We'll get back to you as soon as possible"
        )
        alertDialogBuilder.setPositiveButton("Ok") { dialog, _ -> dialog.dismiss() }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }
}