package com.certified.order.view

import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.bumptech.glide.Glide
import com.certified.order.R
import com.certified.order.databinding.DialogEditProfileBinding
import com.certified.order.databinding.FragmentProfileBinding
import com.certified.order.util.PreferenceKeys
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ProfileFragment : Fragment(), View.OnClickListener {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentProfileBinding
    private lateinit var preferences: SharedPreferences
    private var name: String? = null
    private var email: String? = null
    private var phone: String? = null
    private var defaultDeliveryAddress: String? = null
    private var profileImageUri: Uri? = null
    private var accountType: String? = null

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

        loadUserDetails()


    }

    private fun loadUserDetails() {
        name = preferences.getString(PreferenceKeys.USER_NAME, "")
        email = preferences.getString(PreferenceKeys.USER_EMAIL, "")
        phone = preferences.getString(PreferenceKeys.USER_PHONE, "")
        defaultDeliveryAddress =
            preferences.getString(PreferenceKeys.USER_DEFAULT_DELIVERY_ADDRESS, "")
        profileImageUri = auth.currentUser!!.photoUrl
        accountType = preferences.getString(PreferenceKeys.ACCOUNT_TYPE, "")

        binding.apply {
            tvName.text = name
            tvEmail.text = email
            tvPhone.text = phone
            tvDefaultDeliveryAddress.text = defaultDeliveryAddress
            if (profileImageUri == null)
                Glide.with(requireContext())
                    .load(R.drawable.no_profile_image)
                    .into(profileImage)
            else
                Glide.with(requireContext())
                    .load(profileImageUri)
                    .into(profileImage)
            if (accountType == "Dispatcher")
                cardViewAddress.visibility = View.GONE
        }
    }

    override fun onClick(v: View?) {
        binding.apply {
            when (v) {
                tvName -> launchEditDialog("name")
                tvEmail -> launchEditDialog("email")
                tvPhone -> launchEditDialog("phone")
                tvPassword -> launchEditDialog("password")
                tvDefaultDeliveryAddress -> launchEditDialog("address")
            }
        }
    }

    private fun launchEditDialog(type: String) {
        val binding =
            DialogEditProfileBinding.inflate(
                layoutInflater,
                ConstraintLayout(requireContext()),
                false
            )
        val bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
        binding.apply {
            when (type) {
                "name" -> {
                    tvTitle.text = "Name"
                    etTitle.setText(name)
                }
                "email" -> {
                    tvTitle.text = "Email"
                    etTitle.setText(email)
                }
                "phone" -> {
                    tvTitle.text = "Phone"
                    etTitle.setText(phone)

                }
                "password" -> {
                    tvTitle.text = "Password"
                    etTitle.hint = getString(R.string.min_8_characters)

                }
                "address" -> {
                    tvTitle.text = "Address"
                    etTitle.setText(defaultDeliveryAddress)

                }
            }
        }
        bottomSheetDialog.setContentView(binding.root)
        bottomSheetDialog.show()
    }
}