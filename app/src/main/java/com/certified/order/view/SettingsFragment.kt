package com.certified.order.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.preference.PreferenceManager
import com.bumptech.glide.Glide
import com.certified.order.R
import com.certified.order.databinding.FragmentSettingsBinding
import com.certified.order.util.PreferenceKeys
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SettingsFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentSettingsBinding
    private lateinit var navController: NavController
    private var accountType: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(layoutInflater)

        auth = Firebase.auth

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user = auth.currentUser
        navController = Navigation.findNavController(view)

        if (user != null) {
            loadUserDetails(user)
            checkAccountType(user)
        }

        binding.apply {
            groupMyProfile.setOnClickListener { navController.navigate(R.id.profileFragment) }
            groupMyAddress.setOnClickListener { showMap() }
            groupPaymentMethods.setOnClickListener {
                Toast.makeText(requireContext(), "", Toast.LENGTH_LONG).show()
            }

            val preferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
            val nightMode = preferences.getInt(PreferenceKeys.DARK_MODE, 0)
            val editor = preferences.edit()

            switchDarkMode.isChecked = nightMode == 1
            switchDarkMode.setOnClickListener {
                if (switchDarkMode.isChecked) {
                    editor.putInt(PreferenceKeys.DARK_MODE, 1)
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                } else {
                    editor.putInt(PreferenceKeys.DARK_MODE, 2)
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
                editor.apply()
            }

            btnSignout.setOnClickListener {
                auth.signOut()
                val navOptions = NavOptions.Builder()
                navOptions.setPopUpTo(R.id.onboardingFragment, true)
                navController.navigate(R.id.onboardingFragment)
            }
        }
    }

    private fun showMap() {
        TODO("Not yet implemented")
    }

    private fun loadUserDetails(user: FirebaseUser?) {
        val db = Firebase.firestore
        val userRef =
            db.collection("accounts").document("users")
                .collection(user!!.uid).document("details")
        userRef.get().addOnSuccessListener {
            if (it.exists()) {
                val phone = it.getString("phone")
                val profileImageUri = user.photoUrl

                binding.apply {
                    if (profileImageUri != null)
                        Glide.with(requireContext())
                            .load(profileImageUri)
                            .into(profileImage)
                    else
                        Glide.with(requireContext())
                            .load(R.drawable.no_profile_image)
                            .into(profileImage)

                    tvName.text = user.displayName
                    tvEmail.text = user.email
                    tvPhone.text = phone
                }
            }
        }
    }

    private fun checkAccountType(user: FirebaseUser) {
        val db = Firebase.firestore
        val userRef =
            db.collection("account_type").document(user.uid)
        userRef.get().addOnSuccessListener {
            if (it.exists()) {
                accountType = it.getString("account_type")
                binding.apply {
                    if (accountType == "dispatcher") {
                        groupMyAddress.visibility = View.GONE
                        groupPaymentMethods.visibility = View.GONE
                        groupMyOrders.setOnClickListener {
//                            TODO: Show the dispatchers completed orders
                            TODO( "Not yet implemented")
                        }
                    } else
                        groupMyOrders.setOnClickListener { navController.navigate(R.id.myOrdersFragment) }
                }
            }
        }
    }
}