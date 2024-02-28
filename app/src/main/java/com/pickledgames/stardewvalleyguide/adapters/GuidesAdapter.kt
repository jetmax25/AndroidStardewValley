package com.pickledgames.stardewvalleyguide.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pickledgames.stardewvalleyguide.databinding.ListItemGuideBinding
import kotlinx.android.extensions.LayoutContainer

class GuidesAdapter(
        private val guides: List<String>
) : RecyclerView.Adapter<GuidesAdapter.GuideViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GuideViewHolder {
        val binding = ListItemGuideBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GuideViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return guides.size
    }

    override fun onBindViewHolder(holder: GuideViewHolder, position: Int) {
        holder.bindGuide(guides[position])
    }

    class GuideViewHolder(
        private val binding: ListItemGuideBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindGuide(guide: String) {
            binding.listItemGuideTextView.text = guide
        }
    }
}
