package com.pickledgames.stardewvalleyguide.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import com.pickledgames.stardewvalleyguide.R
import com.pickledgames.stardewvalleyguide.activities.MainActivity
import com.pickledgames.stardewvalleyguide.fragments.VillagerFragment
import com.pickledgames.stardewvalleyguide.interfaces.Sortable
import com.pickledgames.stardewvalleyguide.models.Villager
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.list_item_villager.*

class VillagersAdapter(
        private val villagers: List<Villager>,
        private val mainActivity: MainActivity
) : RecyclerView.Adapter<VillagersAdapter.VillagerViewHolder>(), Filterable, Sortable {

    private var filteredVillagers: MutableList<Villager> = villagers.toMutableList()
    private var sortBy: String = "A-Z"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VillagerViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.list_item_villager, parent, false)
        return VillagerViewHolder(v, mainActivity)
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

    fun setSortBy(s: String) {
        sortBy = s
        sort()
    }

    override fun sort() {
        val comparator = when (sortBy) {
            "A-Z" -> Comparator { v1, v2 -> v1.name.compareTo(v2.name) }
            "Z-A" -> Comparator { v1, v2 -> v2.name.compareTo(v1.name) }
            "Romanceable" -> Comparator { v1, v2 -> v2.canMarry.compareTo(v1.canMarry) }
            else -> Comparator<Villager> { v1, v2 -> v1.name.compareTo(v2.name) }
        }

        filteredVillagers.sortWith(comparator)
        notifyDataSetChanged()
    }

    class VillagerViewHolder(
            override val containerView: View,
            private val mainActivity: MainActivity
    ) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun bindVillager(villager: Villager) {
            villager_name_text_view?.text = villager.name
            if (villager.canMarry) villager_name_text_view?.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_heart_red, 0)
            else villager_name_text_view?.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)

            villager_image_view?.setImageResource(villager.getImageId(mainActivity))
            villager_image_view?.contentDescription = villager.name
            containerView.setOnClickListener {
                mainActivity.pushFragment(VillagerFragment.newInstance(villager))
            }
        }
    }
}
