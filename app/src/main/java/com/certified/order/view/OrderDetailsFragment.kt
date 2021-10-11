package com.certified.order.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.certified.order.ItemViewModel
import com.certified.order.ItemViewModelFactory
import com.certified.order.R
import com.certified.order.adapter.ItemAdapter
import com.certified.order.databinding.FragmentOrderDetailsBinding
import com.certified.order.model.Item
import com.certified.order.model.Order
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OrderDetailsFragment(private val order: Order) : DialogFragment() {

    private lateinit var binding: FragmentOrderDetailsBinding
    private lateinit var currentLatLng: LatLng

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentOrderDetailsBinding.inflate(layoutInflater)

        currentLatLng = getCurrentLocation() ?: LatLng(-34.0, 151.0)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val viewModelFactory = ItemViewModelFactory(order.items)
        val viewModel: ItemViewModel by lazy {
            ViewModelProvider(this, viewModelFactory).get(ItemViewModel::class.java)
        }

        viewModel.showProgressBar.observe(viewLifecycleOwner) {
            if (it)
                binding.progressBar.visibility = View.VISIBLE
            else
                binding.progressBar.visibility = View.GONE
        }

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        val adapter = ItemAdapter(order.items!!)
        binding.recyclerViewItems.adapter = adapter
        binding.recyclerViewItems.layoutManager = LinearLayoutManager(requireContext())

        adapter.setOnItemClickedListener(object : ItemAdapter.OnItemClickedListener {
            override fun onItemClick(item: Item) {

            }

        })

        loadOrderDetails(order)

        binding.apply {

            btnCloseDialog.setOnClickListener { dismiss() }
            if (order.dispatcher_name.isNotEmpty())
                chipItemStatus.setOnClickListener {
                    binding.progressBar.visibility = View.VISIBLE
                    when (chipItemStatus.text.toString()) {
                        "Pending" -> {
                            chipItemStatus.text = resources.getString(R.string.delivered)
                            chipItemStatus.isChecked = true

                            chipItemStatus.isClickable = false

//                            Update the order details in firestore and add it to the dispatchers completed orders
                            val orderRef =
                                Firebase.firestore.collection("dispatcher_orders")
                                    .document(Firebase.auth.currentUser!!.uid)
                                    .collection("my_completed_orders").document()
                            order.id = orderRef.id
                            orderRef.set(order)
                                .addOnSuccessListener { binding.progressBar.visibility = View.GONE }
                        }
                        "Delivered" -> {
                            chipItemStatus.isChecked = true
                            chipItemStatus.isClickable = false
                        }
                    }
                }
            else chipItemStatus.isClickable = false
        }
    }

    private fun loadOrderDetails(order: Order) {
        binding.apply {
            val name = order.receiver_name
            val profileImage = order.receiver_photourl
            val phoneNo = order.receiver_phone_no
            val latitude = order.latitude?.toDouble()
            val longitude = order.latitude?.toDouble()
            val isDelivered = order.is_delivered
            val deliveryTime = order.delivery_time
            val subTotal = order.subtotal

            if (profileImage != null)
                Glide.with(requireContext())
                    .load(profileImage)
                    .into(receiverProfileImage)
            else {
                Glide.with(requireContext())
                    .load(R.drawable.no_profile_image)
                    .into(receiverProfileImage)
            }

            tvReceiverName.text = name
            tvDeliveryTime.text = deliveryTime
            tvSubtotal.text = subTotal.toString()

            if (isDelivered)
                chipItemStatus.text = resources.getString(R.string.delivered)
            else
                chipItemStatus.text = resources.getString(R.string.pending)

            btnCallReceiver.setOnClickListener {
                startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel: $phoneNo")))
            }

            loadDirections(LatLng(latitude!!, longitude!!))
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

    private fun loadDirections(latLng: LatLng) {
        val fragmentManager = requireActivity().supportFragmentManager
        val mapsFragment = MapsFragment(null, latLng)
        val transaction = fragmentManager.beginTransaction()
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        transaction
            .replace(R.id.fragment_location, mapsFragment)
            .addToBackStack(null)
            .commit()
    }
}