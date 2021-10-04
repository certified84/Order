package com.certified.order.view

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
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
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SettingsFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentSettingsBinding
    private lateinit var navController: NavController
    private lateinit var preferences: SharedPreferences
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

        navController = Navigation.findNavController(view)
        preferences = PreferenceManager.getDefaultSharedPreferences(requireContext())

        loadUserDetails()
        checkAccountType()

        binding.apply {

            tvMyProfile.setOnClickListener { navController.navigate(R.id.profileFragment) }
            tvMyAddress.setOnClickListener { showMap() }
            tvPaymentMethods.setOnClickListener {
                Toast.makeText(requireContext(), "", Toast.LENGTH_LONG).show()
            }
            tvMyOrders.setOnClickListener { navController.navigate(R.id.myOrdersFragment) }

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

            tvAboutUs.setOnClickListener {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://lyticaltechnology.com/#about")
                    )
                )
            }
            tvContactUs.setOnClickListener {
//                TODO: Open mail app to .....
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:")
                    putExtra(Intent.EXTRA_EMAIL, "info@lyticaltechnology.com")
                    putExtra(Intent.EXTRA_SUBJECT, "Enquiry")
                }
                if (intent.resolveActivity(requireActivity().packageManager) != null)
                    startActivity(intent)
            }

            btnSignout.setOnClickListener {
                auth.signOut()
                editor.putString(PreferenceKeys.ACCOUNT_TYPE, "")
                editor.putString(PreferenceKeys.USER_NAME, "")
                editor.putString(PreferenceKeys.USER_EMAIL, "")
                editor.putString(PreferenceKeys.USER_PHONE, "")
                editor.putBoolean(PreferenceKeys.IS_FIRST_LOGIN, true)
                editor.putBoolean(PreferenceKeys.IS_APPROVED, false)
                editor.apply()
                val navOptions = NavOptions.Builder().setPopUpTo(R.id.onboardingFragment, true).build()
                navController.popBackStack()
                navController.navigate(R.id.onboardingFragment)
            }
        }
    }

    private fun showMap() {
        TODO("Not yet implemented")
    }

    private fun loadUserDetails() {
        val name = preferences.getString(PreferenceKeys.USER_NAME, "")
        val email = preferences.getString(PreferenceKeys.USER_EMAIL, "")
        val phone = preferences.getString(PreferenceKeys.USER_PHONE, "")
        val profileImageUri = auth.currentUser?.photoUrl
        accountType = preferences.getString("account_type", "")

        binding.apply {
            if (profileImageUri != null)
                Glide.with(requireContext())
                    .load(profileImageUri)
                    .into(profileImage)
            else
                Glide.with(requireContext())
                    .load(R.drawable.no_profile_image)
                    .into(profileImage)
            tvName.text = name
            tvEmail.text = email
            tvPhone.text = phone
        }
    }

    private fun checkAccountType() {
        binding.apply {
            if (accountType == "Dispatcher") {
                tvMyOrders.visibility = View.GONE
                tvMyAddress.visibility = View.GONE
                tvPaymentMethods.visibility = View.GONE
            }
        }
    }
}