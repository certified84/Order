package com.certified.order.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavOptions
import com.bumptech.glide.Glide
import com.certified.order.R
import com.certified.order.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ProfileFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(layoutInflater)

        auth = Firebase.auth

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user = auth.currentUser

        if (user != null)
            loadUserDetails(user)
    }

    private fun loadUserDetails(user: FirebaseUser) {
        val db = Firebase.firestore
        val userRef = db.collection("users").document(user.uid)
        userRef.get().addOnSuccessListener {
            if (it.exists()) {
                val accountType = it.getString("account_type")
                val phone = it.getLong("phone")
                val profile_image_uri = it.getString("profile_image")

                binding.apply {
                    if (profile_image_uri != null)
                        Glide.with(requireContext())
                            .load(profile_image_uri)
                            .into(profileImage)

                    tvEmail.text = user.email
                    tvName.text = user.displayName
                    tvPhone.text = phone.toString()
                }
            }
        }
    }
}