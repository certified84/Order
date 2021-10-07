package com.certified.order.view

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.preference.PreferenceManager
import com.certified.order.R
import com.certified.order.databinding.FragmentLoginBinding
import com.certified.order.util.PreferenceKeys
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginFragment : DialogFragment() {

    private lateinit var navController: NavController
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentLoginBinding
    private lateinit var preferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(layoutInflater)

        auth = Firebase.auth

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(requireActivity().findViewById(R.id.fragment))
        preferences = PreferenceManager.getDefaultSharedPreferences(requireContext())

        binding.apply {
            btnLogin.setOnClickListener {

                val email = etEmail.text.toString().trim()
                val password = etPassword.text.toString().trim()

                if (email.isNotEmpty() && password.isNotEmpty()) {
                    progressBar.visibility = View.VISIBLE

                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(requireActivity()) { task ->
                            if (task.isSuccessful) {

                                progressBar.visibility = View.GONE

                                val user = auth.currentUser

                                if (user?.isEmailVerified!!)
                                    checkAccountType(user)
                                else
                                    Toast.makeText(
                                        requireContext(),
                                        "Check your email for verification link",
                                        Toast.LENGTH_LONG
                                    ).show()
                            } else {

                                progressBar.visibility = View.GONE

                                Toast.makeText(
                                    requireContext(),
                                    "Authentication failed. ${task.exception}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                } else
                    Toast.makeText(
                        requireContext(),
                        "All fields are required",
                        Toast.LENGTH_LONG
                    ).show()
            }

            root.setOnClickListener { super.dismiss() }

            btnSignup.setOnClickListener {
                super.dismiss()
                CoroutineScope(Dispatchers.IO).launch {
                    showSignupDialog()
                }
            }
        }
    }

    private fun showSignupDialog() {
        val isLargeLayout = resources.getBoolean(R.bool.large_layout)
        val fragmentManager = requireActivity().supportFragmentManager
        val signupFragment = SignupFragment()
//        if (isLargeLayout) {
//            signupFragment.show(fragmentManager, "signupFragment")
//        } else {
        val transaction = fragmentManager.beginTransaction()
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        transaction
            .add(android.R.id.content, signupFragment)
            .addToBackStack(null)
            .commit()
//        }
    }

    private fun checkAccountType(user: FirebaseUser) {
        val db = Firebase.firestore
        val userRef =
            db.collection("account_type").document(user.uid)
        userRef.get().addOnSuccessListener {
            if (it.exists()) {
                val accountType = it.getString("account_type")
                val isApproved = it.getBoolean("_approved")
                if (accountType == "Dispatcher" && !isApproved!!)
                    showDialog()
                else {
                    saveDetails(db, accountType, isApproved)
                    dismiss()
                    val navOptions = NavOptions.Builder()
                        .setPopUpTo(R.id.onboardingFragment, true).build()
                    navController.navigate(R.id.homeFragment, null, navOptions)
                }
            }
        }
    }

    private fun saveDetails(db: FirebaseFirestore, accountType: String?, isApproved: Boolean?) {
        val editor = preferences.edit()
        editor.putString(PreferenceKeys.ACCOUNT_TYPE, accountType)
        editor.putBoolean(PreferenceKeys.IS_APPROVED, isApproved!!)
        editor.putBoolean(PreferenceKeys.IS_FIRST_LOGIN, false)
        editor.putString(PreferenceKeys.USER_NAME, auth.currentUser?.displayName)
        editor.putString(PreferenceKeys.USER_EMAIL, auth.currentUser?.email)

        val userRef = if (accountType == "Dispatcher")
            db.collection("accounts").document("dispatchers")
                .collection(auth.currentUser!!.uid).document("details")
        else
            db.collection("accounts").document("users")
                .collection(auth.currentUser!!.uid).document("details")
        userRef.get().addOnSuccessListener {
            if (it.exists()) {
                val phone = it.getString("phone")
                val defaultAddress = it.getString("default_Address")
                editor.putString(PreferenceKeys.USER_PHONE, phone)
                editor.putString(PreferenceKeys.USER_DEFAULT_DELIVERY_ADDRESS, defaultAddress)
                editor.apply()
            }
        }
        editor.apply()
    }

    private fun showDialog() {
        val alertDialogBuilder = MaterialAlertDialogBuilder(requireContext())
        alertDialogBuilder.setTitle("Dispatcher account")
        alertDialogBuilder.setMessage(
            "You registered your account as a dispatcher. Someone from our team will reach out to you with more details"
        )
        alertDialogBuilder.setPositiveButton("Ok") { dialog, _ -> dialog.dismiss() }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
        Firebase.auth.signOut()
    }
}