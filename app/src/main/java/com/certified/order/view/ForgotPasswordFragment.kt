package com.certified.order.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.certified.order.databinding.FragmentForgotPasswordBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ForgotPasswordFragment : DialogFragment() {

    private var _binding: FragmentForgotPasswordBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentForgotPasswordBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {

            root.setOnClickListener { super.dismiss() }
            btnResetPassword.setOnClickListener {
                val email = etEmail.text.toString()
                if (email.isBlank()) {
                    etEmail.error = "Required *"
                    return@setOnClickListener
                }

                progressBar.visibility = View.VISIBLE
                etEmail.error = null
                Firebase.auth.sendPasswordResetEmail(email).addOnCompleteListener {
                    if (it.isSuccessful) {
                        progressBar.visibility = View.GONE
                        Toast.makeText(
                            requireContext(),
                            "An email reset link has been to sent to $email",
                            Toast.LENGTH_LONG
                        ).show()
                        super.dismiss()
                        CoroutineScope(Dispatchers.IO).launch {
                            showLoginDialog()
                        }
                    } else {
                        progressBar.visibility = View.GONE
                        Toast.makeText(
                            requireContext(),
                            "An error occurred: ${it.exception}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}