package com.certified.order.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.RenderMode
import com.certified.order.R
import com.certified.order.databinding.ItemViewPagerBinding
import com.certified.order.model.SliderItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ViewPagerAdapter(private val sliderItem: List<SliderItem>) :
    RecyclerView.Adapter<ViewPagerAdapter.ViewPagerViewHolder>() {

    inner class ViewPagerViewHolder(val binding: ItemViewPagerBinding) : RecyclerView.ViewHolder(binding.root) {

        fun setItems(sliderItem: SliderItem) {
            binding.apply {
                CoroutineScope(Dispatchers.IO).launch {
                    animationView.setRenderMode(RenderMode.SOFTWARE)
                    animationView.setAnimation(sliderItem.animation)
                }
                animationView.enableMergePathsForKitKatAndAbove(true)
                tvTitle.text = sliderItem.title
                tvDescription.text = sliderItem.description
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerViewHolder {
        val binding =
            ItemViewPagerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewPagerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewPagerViewHolder, position: Int) {
        holder.setItems(sliderItem[position])
    }

    override fun getItemCount(): Int {
        return sliderItem.size
    }
}