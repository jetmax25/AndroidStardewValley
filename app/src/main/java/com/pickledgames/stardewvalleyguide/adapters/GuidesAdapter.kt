package com.pickledgames.stardewvalleyguide.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pickledgames.stardewvalleyguide.R
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.list_item_guide.*

class GuidesAdapter(
        private val guides: List<String>
) : RecyclerView.Adapter<GuidesAdapter.GuideViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GuideViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.list_item_guide, parent, false)
        return GuideViewHolder(v)
    }

    override fun getItemCount(): Int {
        return guides.size
    }

    override fun onBindViewHolder(holder: GuideViewHolder, position: Int) {
        holder.bindGuide(guides[position])
    }

    class GuideViewHolder(
            override val containerView: View
    ) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun bindGuide(guide: String) {
            list_item_guide_text_view.text = guide
        }
    }
}
