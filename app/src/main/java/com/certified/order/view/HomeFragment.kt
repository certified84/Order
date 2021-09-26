package com.certified.order.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.certified.order.R
import com.certified.order.adapter.HomeViewPagerAdapter
import com.certified.order.databinding.FragmentHomeBinding
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var navController: NavController

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

        navController = Navigation.findNavController(view)
//        val currentUser = auth.currentUser!!
//        val name = currentUser.displayName?.substringBefore("")
//        val profilePicture = currentUser.photoUrl

        binding.apply {
            tvHiName.text = "Hi Samson"
//            if (profilePicture == null)
                Glide.with(requireContext())
                    .load(R.drawable.no_profile_image)
                    .into(profileImage)
//            else
//                Glide.with(requireContext())
//                    .load(profilePicture)
//                    .into(profileImage)

            cart.setOnClickListener { navController.navigate(R.id.cartFragment) }
            profileImage.setOnClickListener { navController.navigate(R.id.settingsFragment) }


            val adapter = requireActivity().let {
                HomeViewPagerAdapter(
                    it.supportFragmentManager,
                    it.lifecycle
                )
            }

            viewPagerBurgers.adapter = adapter
            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    viewPagerBurgers.currentItem = tab?.position!!
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {

                }

                override fun onTabReselected(tab: TabLayout.Tab?) {

                }

            })

            viewPagerBurgers.registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    tabLayout.selectTab(tabLayout.getTabAt(position))
                }
            })
        }
    }
}