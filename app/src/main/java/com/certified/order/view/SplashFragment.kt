package com.certified.order.view

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.preference.PreferenceManager.getDefaultSharedPreferences
import com.certified.order.R
import com.certified.order.databinding.FragmentSplashBinding
import com.certified.order.util.PreferenceKeys
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SplashFragment : Fragment() {

    private lateinit var binding: FragmentSplashBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var navController: NavController
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "Preferences")
    private lateinit var preferences: SharedPreferences
    private lateinit var navOptions: NavOptions

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSplashBinding.inflate(inflater)

        auth = Firebase.auth

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)
        preferences = getDefaultSharedPreferences(requireContext())
        navOptions = NavOptions.Builder().setPopUpTo(R.id.splashFragment, true).build()

        val w: Window = requireActivity().window
        w.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        w.statusBarColor = Color.TRANSPARENT

        val handler = Handler(Looper.myLooper()!!)
        handler.postDelayed({
            isFirstLogin()
        }, 3000)
    }

    private fun isFirstLogin() {
        val isFirstLogin = preferences.getBoolean(PreferenceKeys.IS_FIRST_LOGIN, true)
        if (isFirstLogin)
            navController.navigate(R.id.onboardingFragment, null, navOptions)
        else
            checkAccountType()
    }

    private fun checkAccountType() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val accountType = preferences.getString(PreferenceKeys.ACCOUNT_TYPE, "")
            val isApproved = preferences.getBoolean(PreferenceKeys.IS_APPROVED, false)
            println("Query: AccountType = $accountType \n isApproved = $isApproved")
            if (accountType == "User" || (accountType == "Dispatcher" && isApproved))
                navController.navigate(R.id.homeFragment, null, navOptions)
        } else
            navController.navigate(R.id.onboardingFragment, null, navOptions)
    }
}