package com.certified.order.view

import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.certified.order.ItemViewModel
import com.certified.order.ItemViewModelFactory
import com.certified.order.R
import com.certified.order.adapter.ItemAdapter
import com.certified.order.databinding.FragmentConfirmOrderBinding
import com.certified.order.model.Item
import com.certified.order.model.Order
import com.google.android.gms.maps.model.LatLng
import java.util.*

class DeliveredOrderDetailsFragment(private val order: Order) : DialogFragment() {

    private lateinit var binding: FragmentConfirmOrderBinding
    private lateinit var currentLatLng: LatLng

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentConfirmOrderBinding.inflate(layoutInflater)

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
        binding.recyclerViewItemsToDeliver.adapter = adapter
        binding.recyclerViewItemsToDeliver.layoutManager = LinearLayoutManager(requireContext())

        adapter.setOnItemClickedListener(object : ItemAdapter.OnItemClickedListener {
            override fun onItemClick(item: Item) {

            }
        })

        binding.apply {

            btnCloseDialog.setOnClickListener { dismiss() }
            btnConfirmOrder.visibility = View.GONE

            tvReceiverName.text = order.receiver_name
            tvDeliveryTime.text = order.delivery_time
            tvReceiverPhone.text = order.receiver_phone_no

            val deliveryAddress = Geocoder(
                requireContext(),
                Locale.getDefault()
            ).getFromLocation(
                order.latitude!!.toDouble(),
                order.longitude!!.toDouble(),
                1
            )[0].getAddressLine(0)
            tvAddress.text = deliveryAddress

            if (order.receiver_photourl == null)
                Glide.with(requireContext())
                    .load(R.drawable.no_profile_image).into(receiverProfileImage)
            else
                Glide.with(requireContext())
                    .load(order.dispatcher_photourl).into(receiverProfileImage)
        }
    }
}