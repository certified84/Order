package com.certified.order.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.RenderMode
import com.certified.order.databinding.ItemViewPagerBinding
import com.certified.order.model.OnboardingItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OnboardingViewPagerAdapter(private val onboardingItem: List<OnboardingItem>) :
    RecyclerView.Adapter<OnboardingViewPagerAdapter.ViewPagerViewHolder>() {

    inner class ViewPagerViewHolder(val binding: ItemViewPagerBinding) : RecyclerView.ViewHolder(binding.root) {

        fun setItems(onboardingItem: OnboardingItem) {
            binding.apply {
                CoroutineScope(Dispatchers.IO).launch {
                    animationView.setRenderMode(RenderMode.SOFTWARE)
                    animationView.setAnimation(onboardingItem.animation)
                }
                animationView.enableMergePathsForKitKatAndAbove(true)
                tvTitle.text = onboardingItem.title
                tvDescription.text = onboardingItem.description
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerViewHolder {
        val binding =
            ItemViewPagerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewPagerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewPagerViewHolder, position: Int) {
        holder.setItems(onboardingItem[position])
    }

    override fun getItemCount(): Int {
        return onboardingItem.size
    }
}