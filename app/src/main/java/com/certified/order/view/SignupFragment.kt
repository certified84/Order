package com.certified.order.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentTransaction
import com.certified.order.databinding.FragmentSignupBinding
import com.certified.order.model.Dispatcher
import com.certified.order.model.AccountType
import com.certified.order.util.Config
import com.certified.order.util.Mailer
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.certified.order.model.User as nUser

class SignupFragment : DialogFragment() {

//    private lateinit var navController: NavController
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

//        navController = Navigation.findNavController(view)
        val currentUser = auth.currentUser

        binding.apply {

            val accountTypes = arrayListOf("Select account type", "Dispatcher", "User")
            val arrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, accountTypes)
            spinnerAccountType.adapter = arrayAdapter

            btnSignup.setOnClickListener {

                val name = etName.text.toString().trim()
                val phone = etPhone.text.toString().trim()
                val email = etEmail.text.toString().trim()
                val password = etPassword.text.toString().trim()
                val accountType = spinnerAccountType.selectedItem.toString()

//                if (currentUser == null) {
                    if (email.isNotEmpty() && password.isNotEmpty() && name.isNotEmpty() && phone.isNotEmpty()) {
                        if (accountType != "Select account type") {

                            progressBar.visibility = View.VISIBLE

                            auth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(requireActivity()) { task ->
                                    if (task.isSuccessful) {
                                        val user = auth.currentUser

                                        user!!.sendEmailVerification()

                                        val db = Firebase.firestore
                                        val newAccountType =
                                            db.collection("account_type")
                                                .document(user.uid)
                                        newAccountType.set(AccountType(accountType))

                                        if (accountType == "Dispatcher") {

                                            val newDispatcher = Dispatcher(name, phone)
                                            newDispatcher.id = user.uid
                                            newDispatcher.email = user.email.toString()

                                            val userRef =
                                                db.collection("accounts").document("dispatchers")
                                                    .collection(user.uid).document("details")
                                            userRef.set(newDispatcher).addOnCompleteListener {
                                                if (it.isSuccessful) {
                                                    val profileChangeRequest =
                                                        UserProfileChangeRequest.Builder()
                                                            .setDisplayName(newDispatcher.name)
                                                            .setPhotoUri(newDispatcher.profile_image)
                                                            .build()
                                                    user.updateProfile(profileChangeRequest)

                                                    showDialog()
                                                    mailAdmin()
                                                    mailUser()

                                                    Firebase.auth.signOut()
                                                } else
                                                    Toast.makeText(
                                                        requireContext(),
                                                        "An error occurred: ${it.exception}",
                                                        Toast.LENGTH_LONG
                                                    ).show()
                                            }
                                        } else {
                                            val newUser =
                                                nUser(name, phone)
                                            newUser.id = user.uid
                                            newUser.email = user.email.toString()

                                            val userRef =
                                                db.collection("accounts").document("users")
                                                    .collection(user.uid).document("details")
                                            userRef.set(newUser).addOnCompleteListener {
                                                if (it.isSuccessful) {
                                                    val profileChangeRequest =
                                                        UserProfileChangeRequest.Builder()
                                                            .setDisplayName(newUser.name)
                                                            .setPhotoUri(newUser.profile_image)
                                                            .build()
                                                    user.updateProfile(profileChangeRequest)

                                                    Firebase.auth.signOut()
                                                } else
                                                    Toast.makeText(
                                                        requireContext(),
                                                        "An error occurred: ${it.exception}",
                                                        Toast.LENGTH_LONG
                                                    ).show()
                                            }
                                        }

                                        progressBar.visibility = View.GONE

                                        Toast.makeText(
                                            requireContext(),
                                            "Success. Check email for verification link",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        Firebase.auth.signOut()

                                    } else {
                                        Toast.makeText(
                                            requireContext(),
                                            "Registration failed ${task.exception}",
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
//                }
            }

            root.setOnClickListener { super.dismiss() }

            tvLogin.setOnClickListener {
                super.dismiss()
                CoroutineScope(Dispatchers.IO).launch {
                    showLoginDialog()
                }
            }
        }
    }

    @SuppressLint("CheckResult")
    private fun mailUser() {
        binding.apply {
            val email = etEmail.text.toString()
            val name = etName.text.toString().substringBefore(" ")
            val subject = "Registration received"
            val message = "Dear $name \n\n" +
                    "Your registration to become a dispatcher on our ORDER app has been received.\n" +
                    "We'll get back to you with more details shortly. \n\n" +
                    "Regards,\n" +
                    "Order app Team."

            Mailer.sendMail(email, subject, message).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
        }
    }

    @SuppressLint("CheckResult")
    private fun mailAdmin() {
        binding.apply {
        val email = Config.ADMIN_EMAIL
        val subject = "New dispatcher registration"
        val message = "A new account has been registered as a dispatcher. Find details below\n" +
                "Name: ${etName.text.toString()}\n" +
                "Email: ${etEmail.text.toString()}\n" +
                "Phone: ${etPhone.text.toString()}"

        Mailer.sendMail(email, subject, message).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
        }
    }

    private fun showDialog() {
        val alertDialogBuilder = MaterialAlertDialogBuilder(requireContext())
        alertDialogBuilder.setTitle("Application sent")
        alertDialogBuilder.setMessage(
            "Your application to become a dispatcher has been sent. We'll get back to you as soon as possible"
        )
        alertDialogBuilder.setPositiveButton("Ok") { dialog, _ -> dialog.dismiss() }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun showLoginDialog() {
//        val isLargeLayout = resources.getBoolean(R.bool.large_layout)
        val fragmentManager = requireActivity().supportFragmentManager
        val loginFragment = LoginFragment()
//        if (isLargeLayout) {
//            loginFragment.show(fragmentManager, "loginFragment")
//        } else {
            val transaction = fragmentManager.beginTransaction()
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            transaction
                .add(android.R.id.content, loginFragment)
                .addToBackStack(null)
                .commit()
//        }
    }
}