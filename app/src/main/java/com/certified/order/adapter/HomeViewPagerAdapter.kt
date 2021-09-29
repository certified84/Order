package com.certified.order.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.certified.order.ItemFragment
import com.certified.order.view.ReviewFragment

class HomeViewPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    private val accountType: String
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        if (accountType == "user")
            return when (position) {
                1 -> ReviewFragment()
                else -> ItemFragment()
            }
        else
            return when (position) {
                1 -> ReviewFragment()
                else -> ItemFragment()
            }
    }
}