package com.certified.order.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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

    private lateinit var binding: ItemViewPagerBinding

    inner class ViewPagerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

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
        binding = ItemViewPagerBinding.inflate(LayoutInflater.from(parent.context))
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_view_pager, parent, false)
        return ViewPagerViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewPagerViewHolder, position: Int) {
        holder.setItems(sliderItem[position])
    }

    override fun getItemCount(): Int {
        return sliderItem.size
    }
}