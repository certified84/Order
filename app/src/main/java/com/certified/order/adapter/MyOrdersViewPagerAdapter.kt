package com.certified.order.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.certified.order.view.DeliveredOrdersFragment
import com.certified.order.view.PendingOrdersFragment

class MyOrdersViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle): FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            1 -> DeliveredOrdersFragment()
            else -> PendingOrdersFragment()
        }
    }
}