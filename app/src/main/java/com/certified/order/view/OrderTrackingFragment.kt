package com.certified.order.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.certified.order.R
import com.certified.order.databinding.FragmentOrderTrackingBinding
import com.certified.order.model.Order
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class OrderTrackingFragment(private val order: Order) : DialogFragment() {

    private lateinit var binding: FragmentOrderTrackingBinding
    private lateinit var auth: FirebaseAuth
    private var currentLatLng: LatLng? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderTrackingBinding.inflate(layoutInflater)

        auth = Firebase.auth
        currentLatLng = getCurrentLocation()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadDirection(
            LatLng(
                order.latitude!!.toDouble(),
                order.longitude!!.toDouble()
            )
        )

        binding.apply {
            val geocoder = Geocoder(requireContext(), Locale.getDefault())
            val orderAddress = geocoder.getFromLocation(
                order.latitude!!.toDouble(),
                order.longitude!!.toDouble(),
                1
            )[0].getAddressLine(0)

            tvDeliveryTime.text = order.delivery_time
            tvDeliveryAddress.text = orderAddress
            tvDispatcherName.text = order.dispatcher_name
            if (order.dispatcher_photourl == null)
                Glide.with(requireContext())
                    .load(R.drawable.no_profile_image).into(dispatcherImage)
            else
                Glide.with(requireContext())
                    .load(order.dispatcher_photourl).into(dispatcherImage)

            btnCloseDialog.setOnClickListener { dismiss() }
            btnCallDispatcher.setOnClickListener {
                startActivity(
                    Intent(
                        Intent.ACTION_DIAL,
                        Uri.parse("tel: ${order.dispatcher_phone_no}")
                    )
                )
            }
        }
    }

    private fun getCurrentLocation(): LatLng? {
        var currentLatLng: LatLng? = null
        val locationProvider =
            LocationServices.getFusedLocationProviderClient(requireActivity())
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            CoroutineScope(Dispatchers.IO).launch {
                locationProvider.lastLocation.addOnCompleteListener {
                    if (it.isSuccessful) {
                        val result = it.result
                        currentLatLng = LatLng(result.latitude, result.longitude)
                    }
                }
            }
        } else
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                101
            )
        return currentLatLng
    }

    private fun loadDirection(latLng: LatLng) {
        val fragmentManager = requireActivity().supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        val mapsFragment = MapsFragment(currentLatLng, latLng)
        transaction.replace(R.id.fragment_order_tracking, mapsFragment)
            .addToBackStack(null)
            .commit()
    }
}