package com.pickledgames.stardewvalleyguide.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.pickledgames.stardewvalleyguide.R
import com.pickledgames.stardewvalleyguide.activities.MainActivity
import com.pickledgames.stardewvalleyguide.databinding.ListItemVillagerBinding
import com.pickledgames.stardewvalleyguide.fragments.VillagerFragment
import com.pickledgames.stardewvalleyguide.interfaces.Sortable
import com.pickledgames.stardewvalleyguide.models.Villager
import kotlinx.android.extensions.LayoutContainer
import java.util.*
import kotlin.Comparator

class VillagersAdapter(
        private val villagers: List<Villager>,
        private val mainActivity: MainActivity
) : RecyclerView.Adapter<VillagersAdapter.VillagerViewHolder>(), Filterable, Sortable {

    private var filteredVillagers: MutableList<Villager> = villagers.toMutableList()
    private var sortBy: String = mainActivity.getString(R.string.a_z)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VillagerViewHolder {
        val binding = ListItemVillagerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VillagerViewHolder(binding, mainActivity)
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
                val filteredVillagers = villagers.filter { it.name.toLowerCase(Locale.US).contains(constraint.toString().toLowerCase(Locale.US)) }
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
            mainActivity.getString(R.string.a_z) -> Comparator { v1, v2 -> v1.name.compareTo(v2.name) }
            mainActivity.getString(R.string.z_a) -> Comparator { v1, v2 -> v2.name.compareTo(v1.name) }
            mainActivity.getString(R.string.romanceable) -> Comparator { v1, v2 -> v2.canMarry.compareTo(v1.canMarry) }
            else -> Comparator<Villager> { v1, v2 -> v1.name.compareTo(v2.name) }
        }

        filteredVillagers.sortWith(comparator)
        notifyDataSetChanged()
    }

    class VillagerViewHolder(
            private val binding: ListItemVillagerBinding,
            private val mainActivity: MainActivity
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindVillager(villager: Villager) {
            binding.villagerNameTextView.text = villager.name
            if (villager.canMarry) {
                binding.villagerNameTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_heart_red, 0)
            }
            else {
                binding.villagerNameTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            }

            binding.villagerImageView.setImageResource(villager.getImageId(mainActivity))
            binding.villagerImageView.contentDescription = villager.name
            binding.root.setOnClickListener {
                mainActivity.pushFragment(VillagerFragment.newInstance(villager))
            }
        }
    }
}
