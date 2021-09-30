package com.certified.order.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.certified.order.R
import com.certified.order.adapter.HomeViewPagerAdapter
import com.certified.order.databinding.FragmentHomeBinding
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayout.Tab
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var navController: NavController
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

        navController = Navigation.findNavController(view)

        val currentUser = auth.currentUser!!
        val name = currentUser.displayName?.substringBefore("")
        val profilePicture = currentUser.photoUrl

        checkAccountType(currentUser)

        binding.apply {
            cart.setOnClickListener { navController.navigate(R.id.cartFragment) }
            tvHiName.text = "Hi $name"
            if (profilePicture == null)
                Glide.with(requireContext())
                    .load(R.drawable.no_profile_image)
                    .into(profileImage)
            else
                Glide.with(requireContext())
                    .load(profilePicture)
                    .into(profileImage)
            profileImage.setOnClickListener { navController.navigate(R.id.settingsFragment) }


            val adapter = requireActivity().let {
                HomeViewPagerAdapter(
                    it.supportFragmentManager,
                    it.lifecycle,
                    "user"
                )
            }

            viewPagerItems.adapter = adapter
            tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
                override fun onTabSelected(tab: Tab?) {
                    viewPagerItems.currentItem = tab?.position!!
                }

                override fun onTabUnselected(tab: Tab?) {

                }

                override fun onTabReselected(tab: Tab?) {

                }

            })

            viewPagerItems.registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    tabLayout.selectTab(tabLayout.getTabAt(position))
                }
            })
        }
    }

//    override fun onResume() {
//        super.onResume()
//        binding.apply {
//            val itemFragment: FragmentContainerView =
//                viewPagerItems.findViewById(R.id.fragment_items)
//            val controller = itemFragment.findNavController()
//            val itemTab: Tab? = tabLayout.getTabAt(0)
//            chipBurger.setOnClickListener {
//                controller.navigate(R.id.burgerFragment)
//                chipBurger.isChecked = true
//                itemTab?.text = "Burgers"
//                tabLayout.selectTab(itemTab)
//            }
//            chipChickenAndChips.setOnClickListener {
//                controller.navigate(R.id.chickenAndChipsFragment)
//                chipChickenAndChips.isChecked = true
//                itemTab?.text = "Chicken and Chips"
//                tabLayout.selectTab(itemTab)
//            }
//            chipPizza.setOnClickListener {
//                controller.navigate(R.id.pizzaFragment)
//                chipPizza.isChecked = true
//                itemTab?.text = "Pizzas"
//                tabLayout.selectTab(itemTab)
//            }
//            chipShawarma.setOnClickListener {
//                controller.navigate(R.id.shawarmaFragment)
//                chipShawarma.isChecked = true
//                itemTab?.text = "Shawarmas"
//                tabLayout.selectTab(itemTab)
//            }
//        }
//    }

    private fun checkAccountType(user: FirebaseUser) {
        val db = Firebase.firestore
        val userRef =
            db.collection("account_type").document(user.uid)
        userRef.get().addOnSuccessListener {
            if (it.exists()) {
                accountType = it.getString("account_type")
                binding.apply {
                    if (accountType == "dispatcher") {
//                    TODO: change the cart icon to assignment
                        Glide.with(requireContext())
                            .load(R.drawable.ic_baseline_assignment_24)
                            .into(cart)
//                        TODO: hide the chip group
                        chipGroup.visibility = View.GONE
                        cart.setOnClickListener { navController.navigate(R.id.newOrdersFragment) }
                    } else
                        cart.setOnClickListener { navController.navigate(R.id.cartFragment) }
                }
            }
        }
    }
}