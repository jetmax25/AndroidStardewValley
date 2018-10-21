package com.pickledgames.stardewvalleyguide.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pickledgames.stardewvalleyguide.R
import com.pickledgames.stardewvalleyguide.activities.MainActivity
import com.pickledgames.stardewvalleyguide.fragments.VillagerFragment
import com.pickledgames.stardewvalleyguide.models.Villager
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.list_item_villager_birthday.*

class VillagerBirthdaysAdapter(
        private var villagers: List<Villager>,
        private val mainActivity: MainActivity
) : RecyclerView.Adapter<VillagerBirthdaysAdapter.VillagerBirthdayViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VillagerBirthdayViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.list_item_villager_birthday, parent, false)
        return VillagerBirthdayViewHolder(v, mainActivity)
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
            override val containerView: View,
            private val mainActivity: MainActivity
    ) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun bindVillager(villager: Villager) {
            villager_birthday_text_view?.text = villager.birthday.toString()
            villager_birthday_villager_image_view?.setImageResource(villager.getImageId(mainActivity))
            villager_birthday_villager_image_view?.contentDescription = villager.name
            villager_birthday_name_text_view?.text = villager.name

            containerView.setOnClickListener {
                mainActivity.pushFragment(VillagerFragment.newInstance(villager))
            }
        }
    }
}
