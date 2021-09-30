package com.certified.order.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.viewpager2.widget.ViewPager2
import com.certified.order.R
import com.certified.order.adapter.ViewPagerAdapter
import com.certified.order.databinding.FragmentOnboardingBinding
import com.certified.order.model.SliderItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.rd.PageIndicatorView

class OnboardingFragment : Fragment() {

    private lateinit var binding: FragmentOnboardingBinding
    private lateinit var navController: NavController
    private lateinit var auth: FirebaseAuth
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private lateinit var sliderItem: ArrayList<SliderItem>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentOnboardingBinding.inflate(layoutInflater)

        auth = Firebase.auth

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)
        val currentUser = auth.currentUser

//        setUpSliderItem()
        setUpViewPager()

        if (currentUser != null)
            queryDatabase(currentUser)

        binding.btnGetStarted.setOnClickListener { showSignupDialog() }
    }

    private fun showSignupDialog() {
        val fragmentManager = requireActivity().supportFragmentManager
        val signupFragment = SignupFragment()
        val transaction = fragmentManager.beginTransaction()
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        transaction
            .add(android.R.id.content, signupFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun queryDatabase(user: FirebaseUser) {
        val db = Firebase.firestore
        val userRef =
            db.collection("account_type").document(user.uid)
        userRef.get().addOnSuccessListener {
            if (it.exists()) {
                val accountType = it.getString("account_type")
                val isApproved = it.getBoolean("_approved")
                val navOptions = NavOptions.Builder()
                    .setPopUpTo(R.id.onboardingFragment, true).build()

                if (accountType == "user" || (accountType == "dispatcher" && isApproved!!))
                    navController.navigate(R.id.homeFragment, null, navOptions)
            }
        }
    }

    private fun setUpSliderItem() {
        TODO("Not yet implemented")
//        TODO: Download animations from lottie files and edit the title and subtitle of each sliderItem
        sliderItem = arrayListOf(
//            SliderItem(
//                R.raw.animation_note, getString(R.string.view_pager_title_notes),
//                getString(R.string.view_pager_description_notes)
//            ),
//            SliderItem(
//                R.raw.animation_course, getString(R.string.view_pager_title_course),
//                getString(R.string.view_pager_description_courses)
//            ),
//            SliderItem(
//                R.raw.animation_todo, getString(R.string.view_pager_title_todo),
//                getString(R.string.view_pager_description_todos)
//            ),
//            SliderItem(
//                R.raw.animation_report, getString(R.string.view_pager_title_report),
//                getString(R.string.view_pager_description_report)
//            )
        )
    }

    private fun setUpViewPager() {
        binding.apply {
//            viewPagerAdapter = ViewPagerAdapter(sliderItem)
//            viewPager.adapter = viewPagerAdapter
            viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    indicator.selection = position
                    if (position == sliderItem.size - 1) {
                        indicator.count = sliderItem.size
                        indicator.selection = position
                    }
                }
            })
        }
    }
}