package com.certified.order.view

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.preference.PreferenceManager
import androidx.viewpager2.widget.ViewPager2
import com.certified.order.adapter.ItemViewPagerAdapter
import com.certified.order.databinding.FragmentItemBinding
import com.certified.order.util.PreferenceKeys
import com.google.android.material.tabs.TabLayout

class ItemFragment : Fragment() {

    private lateinit var binding: FragmentItemBinding
    private lateinit var navController: NavController
    private lateinit var preferences: SharedPreferences
    private var accountType: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentItemBinding.inflate(layoutInflater)

//        navController = Navigation.findNavController(view)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preferences = PreferenceManager.getDefaultSharedPreferences(requireContext())

        checkAccountType()

        val adapter = requireActivity().let {
            ItemViewPagerAdapter(
                it.supportFragmentManager,
                it.lifecycle,
                accountType!!
            )
        }

        binding.apply {
            viewPagerItems.adapter = adapter
            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    viewPagerItems.currentItem = tab?.position!!
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {

                }

                override fun onTabReselected(tab: TabLayout.Tab?) {

                }
            })

            viewPagerItems.registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    tabLayout.selectTab(tabLayout.getTabAt(position))
                }
            })

            chipBurger.setOnClickListener { tabLayout.getTabAt(0)?.text = "Burgers" }

            chipChickenAndChips.setOnClickListener {
                tabLayout.getTabAt(0)?.text = "Chicken and Chips"
            }

            chipPizza.setOnClickListener { tabLayout.getTabAt(0)?.text = "Pizzas" }

            chipShawarma.setOnClickListener { tabLayout.getTabAt(0)?.text = "Shawarmas" }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.apply {
            if (accountType == "User")
                when {
                    chipBurger.isChecked -> tabLayout.getTabAt(0)?.text = "Burgers"
                    chipChickenAndChips.isChecked -> tabLayout.getTabAt(0)?.text =
                        "Chicken and Chips"
                    chipPizza.isChecked -> tabLayout.getTabAt(0)?.text = "Pizzas"
                    chipShawarma.isChecked -> tabLayout.getTabAt(0)?.text = "Shawarmas"
                }
        }
    }

    private fun checkAccountType() {
        accountType = preferences.getString(PreferenceKeys.ACCOUNT_TYPE, "")
        binding.apply {
            if (accountType == "Dispatcher") {
//                chipGroup.visibility = View.GONE
                tabLayout.getTabAt(0)!!.text = "New Orders"
            }
        }
    }
}