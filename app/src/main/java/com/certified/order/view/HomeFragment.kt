package com.certified.order.view

import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.preference.PreferenceManager
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.certified.order.R
import com.certified.order.adapter.HomeViewPagerAdapter
import com.certified.order.adapter.ItemAdapter
import com.certified.order.databinding.FragmentHomeBinding
import com.certified.order.util.PreferenceKeys
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var navController: NavController
    private lateinit var preferences: SharedPreferences
    private var accountType: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(layoutInflater)

        auth = Firebase.auth

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val w: Window = requireActivity().window
        w.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        w.statusBarColor = Color.BLACK

        navController = Navigation.findNavController(view)
        preferences = PreferenceManager.getDefaultSharedPreferences(requireContext())

        val currentUser = auth.currentUser
        val name = preferences.getString(PreferenceKeys.USER_NAME, "")?.substringBefore(" ")
        val profilePicture = currentUser?.photoUrl

        checkAccountType()

        val adapter = requireActivity().let {
            HomeViewPagerAdapter(
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

            if (accountType == "Dispatcher")
                tabLayout.getTabAt(0)?.text = "New Orders"

            chipBurger.setOnClickListener {
                tabLayout.getTabAt(0)?.text = "Burgers"
                ItemFragment.adapter = ItemAdapter(ItemFragment.items, "burger")
            }
            chipChickenAndChips.setOnClickListener {
                tabLayout.getTabAt(0)?.text = "Chicken and Chips"
                ItemFragment.adapter = ItemAdapter(ItemFragment.items, "chicken and chips")
            }

            chipPizza.setOnClickListener {
                tabLayout.getTabAt(0)?.text = "Pizzas"
                ItemFragment.adapter = ItemAdapter(ItemFragment.items, "pizza")
            }
            chipShawarma.setOnClickListener {
                tabLayout.getTabAt(0)?.text = "Shawarmas"
                ItemFragment.adapter = ItemAdapter(ItemFragment.items, "shawarma")
            }

            tvHiName.text = name
            if (profilePicture == null)
                Glide.with(requireContext())
                    .load(R.drawable.no_profile_image)
                    .into(profileImage)
            else
                Glide.with(requireContext())
                    .load(profilePicture)
                    .into(profileImage)
            profileImage.setOnClickListener { navController.navigate(R.id.settingsFragment) }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.apply {
            if (accountType == "User")
                when {
                    chipAllItems.isChecked -> tabLayout.getTabAt(0)?.text = "All Items"
                    chipBurger.isChecked -> tabLayout.getTabAt(0)?.text = "Burgers"
                    chipChickenAndChips.isChecked -> tabLayout.getTabAt(0)?.text =
                        "Chicken and Chips"
                    chipPizza.isChecked -> tabLayout.getTabAt(0)?.text = "Pizzas"
                    chipShawarma.isChecked -> tabLayout.getTabAt(0)?.text = "Shawarmas"
                }
            else
                chipGroup.visibility = View.GONE
        }
    }

    private fun checkAccountType() {
        accountType = preferences.getString(PreferenceKeys.ACCOUNT_TYPE, "")
        binding.apply {
            if (accountType == "Dispatcher") {
                cart.setImageResource(R.drawable.ic_baseline_assignment_24)
                cart.setOnClickListener { navController.navigate(R.id.completedOrdersFragment) }
            } else
                cart.setOnClickListener { navController.navigate(R.id.cartFragment) }
        }
    }
}