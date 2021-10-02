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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
            groupMyProfile.setOnClickListener { navController.navigate(R.id.profileFragment) }
            groupMyAddress.setOnClickListener { showMap() }
            groupPaymentMethods.setOnClickListener {
                Toast.makeText(requireContext(), "", Toast.LENGTH_LONG).show()
            }

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

            groupAboutUs.setOnClickListener {
//                TODO: lytical technology about us page
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.lyticaltechnology.com/")
                )
            }
            groupContactUs.setOnClickListener {
//                TODO: Open mail app to .....
                Intent(Intent.ACTION_SEND)
            }

            btnSignout.setOnClickListener {
                auth.signOut()
                editor.putString(PreferenceKeys.ACCOUNT_TYPE, "")
                editor.apply()
                val navOptions = NavOptions.Builder()
                navOptions.setPopUpTo(R.id.onboardingFragment, true)
                navController.navigate(R.id.onboardingFragment)
            }
        }
    }

    private fun showMap() {
        TODO("Not yet implemented")
    }

    private fun loadUserDetails() {
        CoroutineScope(Dispatchers.IO).launch {
            val name = preferences.getString(PreferenceKeys.USER_NAME, "")
            val email = preferences.getString(PreferenceKeys.USER_EMAIL, "")
            val phone = preferences.getString(PreferenceKeys.USER_PHONE, "")
            val profileImageUri = auth.currentUser!!.photoUrl
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
    }

    private fun checkAccountType() {
        binding.apply {
            if (accountType == "Dispatcher") {
                tvMyOrders.text = getString(R.string.completed_orders)
                groupMyAddress.visibility = View.GONE
                groupPaymentMethods.visibility = View.GONE
                groupMyOrders.setOnClickListener {
//                            TODO: Show the dispatchers completed orders
                    TODO("Not yet implemented")
                }
            } else
                groupMyOrders.setOnClickListener { navController.navigate(R.id.myOrdersFragment) }
        }
    }
}