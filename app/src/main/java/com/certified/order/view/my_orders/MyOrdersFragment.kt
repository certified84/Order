package com.certified.order.view.my_orders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.certified.order.adapter.MyOrdersViewPagerAdapter
import com.certified.order.databinding.FragmentMyOrdersBinding
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayout.Tab

class MyOrdersFragment() : Fragment() {

    private lateinit var binding: FragmentMyOrdersBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentMyOrdersBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = requireActivity().let {
            MyOrdersViewPagerAdapter(
                it.supportFragmentManager,
                it.lifecycle
            )
        }

        binding.apply {
            viewPagerOrders.adapter = adapter
            tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
                override fun onTabSelected(tab: Tab?) {
                    viewPagerOrders.currentItem = tab?.position!!
                }

                override fun onTabUnselected(tab: Tab?) {

                }

                override fun onTabReselected(tab: Tab?) {

                }

            })

            viewPagerOrders.registerOnPageChangeCallback(object : OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    tabLayout.selectTab(tabLayout.getTabAt(position))
                }
            })
        }
    }
}