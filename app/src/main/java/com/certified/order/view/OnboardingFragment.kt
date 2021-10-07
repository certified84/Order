package com.certified.order.view

import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.viewpager2.widget.ViewPager2
import com.certified.order.R
import com.certified.order.adapter.OnboardingViewPagerAdapter
import com.certified.order.databinding.FragmentOnboardingBinding
import com.certified.order.model.OnboardingItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OnboardingFragment : Fragment() {

    private lateinit var binding: FragmentOnboardingBinding
    private lateinit var navController: NavController
    private lateinit var viewPagerAdapter: OnboardingViewPagerAdapter
    private lateinit var onboardingItem: ArrayList<OnboardingItem>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentOnboardingBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        val w: Window = requireActivity().window
        w.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        w.statusBarColor = Color.BLACK

        setUpSliderItem()
        setUpViewPager()

        binding.btnGetStarted.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                showSignupDialog()
            }
        }
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

    private fun setUpSliderItem() {
        onboardingItem = arrayListOf(
            OnboardingItem(
                R.raw.order_food, "Order your favourites",
                "Pizzas, Burgers, Shawarmas and Chicken & Chips. All your favourites are here"
            ),
            OnboardingItem(
                R.raw.order_tracking, "Track your order",
                "Realtime tracking will keep you posted about your order progress"
            ),
            OnboardingItem(
                R.raw.payment, "Easy payment",
                "Pick a payment method that is convenient works best for you"
            ),
            OnboardingItem(
                R.raw.order_delivery, "Flexible delivery time",
                "When placing an order, you can pick a particular time you'd like your order to be delivered"
            )
        )
    }

    private fun setUpViewPager() {
        binding.apply {
            viewPagerAdapter = OnboardingViewPagerAdapter(onboardingItem)
            viewPager.adapter = viewPagerAdapter
            viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    indicator.selection = position
                    if (position == onboardingItem.size - 1) {
                        indicator.count = onboardingItem.size
                        indicator.selection = position
                    }
                }
            })
        }
    }
}