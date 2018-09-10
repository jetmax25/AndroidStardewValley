package com.pickledgames.stardewvalleyguide.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import com.pickledgames.stardewvalleyguide.R
import com.pickledgames.stardewvalleyguide.interfaces.Sortable
import com.pickledgames.stardewvalleyguide.models.Villager
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.list_item_villager.*

class VillagersAdapter(
        private val villagers: List<Villager>
) : RecyclerView.Adapter<VillagersAdapter.VillagerViewHolder>(), Filterable, Sortable {

    var filteredVillagers: MutableList<Villager> = villagers.toMutableList()
    var sortBy: Int? = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VillagerViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.list_item_villager, parent, false)
        return VillagerViewHolder(v, parent.context)
    }

    override fun getItemCount(): Int {
        return filteredVillagers.size
    }

    override fun onBindViewHolder(holder: VillagerViewHolder, position: Int) {
        holder.bindVillager(filteredVillagers[position])
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                val filteredVillagers = villagers.filter { it.name.toLowerCase().contains(constraint.toString().toLowerCase()) }
                filterResults.values = filteredVillagers
                filterResults.count = filteredVillagers.size
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                @Suppress("UNCHECKED_CAST")
                filteredVillagers = results?.values as MutableList<Villager>
                notifyDataSetChanged()
            }
        }
    }

    override fun sort() {
        val comparator = when (sortBy) {
            0 -> Comparator { v1, v2 -> v1.name.compareTo(v2.name) }
            1 -> Comparator { v1, v2 -> v2.name.compareTo(v1.name) }
            2 -> Comparator { v1, v2 -> v2.canMarry.compareTo(v1.canMarry) }
            else -> Comparator<Villager> { v1, v2 -> v1.name.compareTo(v2.name) }
        }

        filteredVillagers.sortWith(comparator)
        notifyDataSetChanged()
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
