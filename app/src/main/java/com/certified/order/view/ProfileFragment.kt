package com.certified.order.view

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavOptions
import androidx.preference.PreferenceManager
import com.bumptech.glide.Glide
import com.certified.order.R
import com.certified.order.databinding.FragmentProfileBinding
import com.certified.order.util.PreferenceKeys
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentProfileBinding
    private lateinit var preferences: SharedPreferences

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

        preferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val user = auth.currentUser

    }

    private fun loadUserDetails() {
        CoroutineScope(Dispatchers.IO).launch {
            val name = preferences.getString(PreferenceKeys.USER_NAME, "")
            val email = preferences.getString(PreferenceKeys.USER_EMAIL, "")
            val phone = preferences.getString(PreferenceKeys.USER_PHONE, "")
            val profileImageUri = auth.currentUser!!.photoUrl
//            accountType = preferences.getString("account_type", "")
        }
    }
}