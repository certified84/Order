package com.certified.order.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.certified.order.view.ItemFragment
import com.certified.order.view.items.BurgerFragment
import com.certified.order.view.orders.NewOrdersFragment
import com.certified.order.view.review.ReviewFragment

class ItemViewPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    private val accountType: String
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return if (accountType == "User")
            when (position) {
                1 -> ReviewFragment()
                else -> BurgerFragment()
            }
        else
            when (position) {
                1 -> ReviewFragment()
                else -> NewOrdersFragment()
            }
    }
}