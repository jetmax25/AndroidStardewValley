package com.pickledgames.stardewvalleyguide.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pickledgames.stardewvalleyguide.activities.MainActivity
import com.pickledgames.stardewvalleyguide.databinding.ListItemVillagerBirthdayBinding
import com.pickledgames.stardewvalleyguide.fragments.VillagerFragment
import com.pickledgames.stardewvalleyguide.models.Villager

class VillagerBirthdaysAdapter(
        private var villagers: List<Villager>,
        private val mainActivity: MainActivity
) : RecyclerView.Adapter<VillagerBirthdaysAdapter.VillagerBirthdayViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VillagerBirthdayViewHolder {
        val binding = ListItemVillagerBirthdayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VillagerBirthdayViewHolder(binding, mainActivity)
    }

    override fun getItemCount(): Int {
        return villagers.size
    }

    override fun onBindViewHolder(holder: VillagerBirthdayViewHolder, position: Int) {
        holder.bindVillager(villagers[position])
    }

    fun updateVillagers(v: List<Villager>) {
        villagers = v
        notifyDataSetChanged()
    }

    class VillagerBirthdayViewHolder(
            private val binding: ListItemVillagerBirthdayBinding,
            private val mainActivity: MainActivity
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindVillager(villager: Villager) {
            binding.villagerBirthdayTextView.text = villager.birthday.toString()
            binding.villagerBirthdayVillagerImageView.setImageResource(villager.getImageId(mainActivity))
            binding.villagerBirthdayVillagerImageView.contentDescription = villager.name
            binding.villagerBirthdayNameTextView.text = villager.name

            binding.root.setOnClickListener {
                mainActivity.pushFragment(VillagerFragment.newInstance(villager))
            }
        }
    }
}
