package com.pickledgames.stardewvalleyguide.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pickledgames.stardewvalleyguide.activities.MainActivity
import com.pickledgames.stardewvalleyguide.databinding.ListItemFishBinding
import com.pickledgames.stardewvalleyguide.fragments.FishFragment
import com.pickledgames.stardewvalleyguide.interfaces.OnItemCheckedListener
import com.pickledgames.stardewvalleyguide.models.Farm
import com.pickledgames.stardewvalleyguide.models.Fish

class FishesAdapter(
        private var fishes: List<Fish>,
        private var farm: Farm,
        private var showCompleted: Boolean,
        private val mainActivity: MainActivity,
        private val onItemCheckedListener: OnItemCheckedListener
) : RecyclerView.Adapter<FishesAdapter.FishViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FishViewHolder {
        val binding = ListItemFishBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FishViewHolder(binding, mainActivity, onItemCheckedListener)
    }

    override fun getItemCount(): Int {
        return fishes.size
    }

    override fun onBindViewHolder(holder: FishViewHolder, position: Int) {
        val fish = fishes[position]
        val isCompleted = farm.fishes.contains(fish.name)
        holder.bindFish(fish, isCompleted, showCompleted)
    }

    fun updateFarm(f: Farm) {
        farm = f
        notifyDataSetChanged()
    }

    fun updateFishes(f: List<Fish>) {
        fishes = f
        notifyDataSetChanged()
    }

    fun updateShowCompleted(s: Boolean) {
        showCompleted = s
        notifyDataSetChanged()
    }

    class FishViewHolder(
            private val binding: ListItemFishBinding,
            private val mainActivity: MainActivity,
            private val onItemCheckedListener: OnItemCheckedListener
    ) : RecyclerView.ViewHolder(binding.root) {

        private var isChecked: Boolean = false

        fun bindFish(fish: Fish, isCompleted: Boolean, showCompleted: Boolean) {
            if (isCompleted && !showCompleted) {
                binding.root.visibility = View.GONE
                binding.root.layoutParams = RecyclerView.LayoutParams(0, 0)
                return
            } else {
                binding.root.visibility = View.VISIBLE
                binding.root.layoutParams = RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            }

            binding.fishImageView.setImageResource(fish.getImageId(mainActivity))
            binding.fishImageView.contentDescription = fish.name
            binding.fishTextView.text = fish.name
            isChecked = isCompleted
            binding.fishCheckBox.isChecked = isChecked
            // use onClick instead of onCheckChanged to avoid initial firing
            binding.fishCheckBox.setOnClickListener { _ ->
                isChecked = !isChecked
                onItemCheckedListener.onItemChecked(fish, isChecked)
            }
            binding.root.setOnClickListener {
                mainActivity.pushFragment(FishFragment.newInstance(fish))
            }
        }
    }
}
