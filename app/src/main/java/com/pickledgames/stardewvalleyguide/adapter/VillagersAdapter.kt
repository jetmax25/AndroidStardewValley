package com.pickledgames.stardewvalleyguide.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pickledgames.stardewvalleyguide.R
import com.pickledgames.stardewvalleyguide.model.Villager
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.list_item_villager.*

class VillagersAdapter(
        private val villagers: List<Villager>
) : RecyclerView.Adapter<VillagersAdapter.VillagerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VillagerViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.list_item_villager, parent, false)
        return VillagerViewHolder(v, parent.context)
    }

    override fun getItemCount(): Int {
        return villagers.size
    }

    override fun onBindViewHolder(holder: VillagerViewHolder, position: Int) {
        holder.bindVillager(villagers[position])
    }

    class VillagerViewHolder(
            override val containerView: View,
            private val context: Context
    ) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun bindVillager(villager: Villager) {
            villager_name_text_view.text = villager.name
            if (villager.canMarry) villager_name_text_view.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_heart_red, 0)
            else villager_name_text_view.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)

            val id = context.resources.getIdentifier("villager_${villager.name.toLowerCase()}", "drawable", context.packageName)
            villager_image_view.setImageResource(id)
            villager_image_view.contentDescription = villager.name
        }
    }
}
