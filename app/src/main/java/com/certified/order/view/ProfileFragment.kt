package com.certified.order.view

import android.app.Activity
import android.content.*
import android.graphics.Bitmap
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.bumptech.glide.Glide
import com.certified.order.R
import com.certified.order.databinding.DialogEditProfileBinding
import com.certified.order.databinding.FragmentProfileBinding
import com.certified.order.util.PreferenceKeys
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.util.*

class ProfileFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentProfileBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var currentUser: FirebaseUser
    private lateinit var userRef: DocumentReference
    private lateinit var storageRef: StorageReference
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
        currentUser = auth.currentUser!!
        db = Firebase.firestore
        val path = "images/${currentUser.uid}/profile_image.jpg"
        storageRef = FirebaseStorage.getInstance().reference.child(path)

        loadUserDetails()

        userRef = if (accountType == "Dispatcher")
            db.collection("accounts").document("dispatchers")
                .collection(currentUser.uid).document("details")
        else
            db.collection("accounts").document("users")
                .collection(currentUser.uid).document("details")

        binding.apply {
            fabChangeProfilePicture.setOnClickListener { launchChangeProfileImageDialog() }
            profileImage.setOnClickListener { launchChangeProfileImageDialog() }
            tvName.setOnClickListener { launchEditDialog("name") }
            tvEmail.setOnClickListener { launchEditDialog("email") }
            tvPhone.setOnClickListener { launchEditDialog("phone") }
            tvPassword.setOnClickListener { launchEditDialog("password") }
            tvDefaultDeliveryAddress.setOnClickListener {
                val latLng = showMap()
                val geocoder = Geocoder(requireContext(), Locale.getDefault())
                val addresses =
                    geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                val newDefaultDeliveryAddress = addresses[0].getAddressLine(0)
                userRef.update("default_address_line", newDefaultDeliveryAddress)
                    .addOnCompleteListener {

                    }
            }
        }
    }

    private fun launchChangeProfileImageDialog() {
        val builder = MaterialAlertDialogBuilder(requireContext())
        val selection = arrayOf(
//            "View profile picture",
            "Take picture",
            "Choose from gallery",
            "Delete profile picture",
        )
        builder.setTitle("Options")
        builder.setSingleChoiceItems(selection, -1) { dialog: DialogInterface, which: Int ->
            when (which) {
                0 -> launchCamera()
                1 -> chooseFromGallery()
                2 -> {
                    val profileChangeRequest =
                        UserProfileChangeRequest.Builder()
                            .setPhotoUri(null)
                            .build()
                    currentUser.updateProfile(profileChangeRequest)
                    Glide.with(requireContext())
                        .load(R.drawable.no_profile_image)
                        .into(binding.profileImage)
                }
            }
            dialog.dismiss()
        }
        builder.show()
    }

    private fun launchCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {

            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "An error occurred: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun chooseFromGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        //        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        try {
            startActivityForResult(Intent.createChooser(intent, "Select image"), PICK_IMAGE_CODE)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "An error occurred: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun showMap(): LatLng {
        throw Exception("Not yet implemented")
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

    private fun launchEditDialog(type: String) {

        val editor = preferences.edit()

        val view =
            DialogEditProfileBinding.inflate(
                layoutInflater,
                ConstraintLayout(requireContext()),
                false
            )
        val bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
        view.apply {
            when (type) {
                "name" -> {
                    tvTitle.text = getString(R.string.name)
                    etTitle.hint = getString(R.string.first_name_amp_last_name)
                    etTitle.inputType = InputType.TYPE_TEXT_VARIATION_PERSON_NAME
                    etTitle.setText(name)
                    btnUpdate.setOnClickListener {
                        val newName = etTitle.text.toString().trim()
                        if (newName.isNotEmpty()) {
                            if (newName != name) {
                                etTitleLayout.error = null
                                progressBar.visibility = View.VISIBLE
                                editor.putString(PreferenceKeys.USER_NAME, newName)
                                editor.apply()
                                binding.tvName.text = newName
                                userRef.update("name", newName).addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        name = newName
                                        val profileChangeRequest =
                                            UserProfileChangeRequest.Builder()
                                                .setDisplayName(newName)
                                                .build()
                                        currentUser.updateProfile(profileChangeRequest)
                                        progressBar.visibility = View.GONE
                                        bottomSheetDialog.dismiss()
                                    }
                                }
                            } else {
                                bottomSheetDialog.dismiss()
                                Toast.makeText(
                                    requireContext(),
                                    "No change",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        } else
                            etTitleLayout.error = "*Required"
                    }
                }
                "email" -> {
                    tvTitle.text = getString(R.string.email)
                    etTitle.hint = getString(R.string.name_example_etceteria_com)
                    etTitle.setText(email)
                    btnUpdate.setOnClickListener {
                        val newEmail = etTitle.text.toString().trim()
                        if (newEmail.isNotEmpty()) {
                            if (newEmail != email) {
                                progressBar.visibility = View.VISIBLE
                                etTitleLayout.error = null
                                binding.tvEmail.text = newEmail
                                currentUser.updateEmail(newEmail).addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        currentUser.sendEmailVerification()
                                        Toast.makeText(
                                            requireContext(),
                                            "Check email for verification link",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        progressBar.visibility = View.GONE
                                        bottomSheetDialog.dismiss()
                                    }
                                }
                            } else {
                                bottomSheetDialog.dismiss()
                                Toast.makeText(
                                    requireContext(),
                                    "No change",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        } else
                            etTitleLayout.error = "*Required"
                    }
                }
                "phone" -> {
                    tvTitle.text = getString(R.string.phone)
                    etTitle.hint = getString(R.string._123456789)
                    etTitle.inputType = InputType.TYPE_CLASS_PHONE
                    etTitle.setText(phone)
                    btnUpdate.setOnClickListener {
                        val newPhone = etTitle.text.toString().trim()
                        if (newPhone.isNotEmpty()) {
                            if (newPhone != phone) {
                                etTitleLayout.error = null
                                progressBar.visibility = View.VISIBLE
                                editor.putString(PreferenceKeys.USER_PHONE, newPhone)
                                editor.apply()
                                binding.tvPhone.text = newPhone
                                userRef.update("phone", newPhone).addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        phone = newPhone
                                        progressBar.visibility = View.GONE
                                        bottomSheetDialog.dismiss()
                                    }
                                }
                            } else {
                                bottomSheetDialog.dismiss()
                                Toast.makeText(
                                    requireContext(),
                                    "No change",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        } else
                            etTitleLayout.error = "*Required"
                    }
                }
                "password" -> {
                    tvTitle.text = getString(R.string.password)
                    etTitleLayout.endIconMode = TextInputLayout.END_ICON_PASSWORD_TOGGLE
                    etTitle.hint = getString(R.string.min_8_characters)
                    etTitle.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
                    btnUpdate.setOnClickListener {
                        val newPassword = etTitle.text.toString().trim()
                        if (newPassword.isNotEmpty()) {
                            progressBar.visibility = View.VISIBLE
                            etTitleLayout.error = null
                            currentUser.updatePassword(newPassword).addOnCompleteListener {
                                if (it.isSuccessful) {
                                    progressBar.visibility = View.GONE
                                    Toast.makeText(
                                        requireContext(),
                                        "Password changed successfully",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    bottomSheetDialog.dismiss()
                                }
                            }
                        } else
                            etTitleLayout.error = "*Required"
                    }
                }
            }
            btnCancel.setOnClickListener { bottomSheetDialog.dismiss() }
        }
        bottomSheetDialog.setContentView(view.root)
        bottomSheetDialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            assert(data != null)
            val extras = data?.extras
            val profileImageBitmap = extras!!["data"] as Bitmap?
            try {
                requireContext().openFileOutput("profile_image", Context.MODE_PRIVATE).use {
                    profileImageBitmap?.compress(Bitmap.CompressFormat.PNG, 100, it)
                }
                val file = File(requireContext().filesDir, "profile_image")
                val uri = Uri.fromFile(file)
                Glide.with(requireContext())
                    .load(uri)
                    .into(binding.profileImage)
                userRef.update("profile_image", uri.toString())
                storageRef.putFile(uri).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val profileChangeRequest =
                            UserProfileChangeRequest.Builder()
                                .setPhotoUri(uri)
                                .build()
                        currentUser.updateProfile(profileChangeRequest)
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } else if (requestCode == PICK_IMAGE_CODE && resultCode == Activity.RESULT_OK) {
            assert(data != null)
            val uri = data?.data
            try {
//                TODO: Save the Image in Firebase Cloud storage before loading it
                Glide.with(requireContext())
                    .load(uri)
                    .into(binding.profileImage)
                if (uri != null) {
                    storageRef.putFile(uri).addOnCompleteListener {
                        if (it.isSuccessful) {
                            val profileChangeRequest =
                                UserProfileChangeRequest.Builder()
                                    .setPhotoUri(uri)
                                    .build()
                            currentUser.updateProfile(profileChangeRequest)
                        }
                    }
                }
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 101
        private const val PICK_IMAGE_CODE = 102
    }
}