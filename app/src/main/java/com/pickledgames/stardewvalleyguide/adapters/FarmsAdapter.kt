package com.pickledgames.stardewvalleyguide.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pickledgames.stardewvalleyguide.R
import com.pickledgames.stardewvalleyguide.activities.MainActivity
import com.pickledgames.stardewvalleyguide.databinding.ListItemFarmBinding
import com.pickledgames.stardewvalleyguide.fragments.EditFarmDialogFragment
import com.pickledgames.stardewvalleyguide.models.Farm

class FarmsAdapter(
        private val farms: List<Farm>,
        private val mainActivity: MainActivity
) : RecyclerView.Adapter<FarmsAdapter.FarmViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FarmViewHolder {
        val binding = ListItemFarmBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FarmViewHolder(binding, mainActivity)
    }

    override fun getItemCount(): Int {
        return farms.size
    }

    override fun onBindViewHolder(holder: FarmViewHolder, position: Int) {
        holder.bindFarm(farms[position], position)
    }

    class FarmViewHolder(
            private val binding: ListItemFarmBinding,
            private val mainActivity: MainActivity
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindFarm(farm: Farm, position: Int) {
            binding.farmNameTextView.text = String.format(mainActivity.resources.getString(R.string.farm_name_template), farm.name)
            binding.farmTypeTextView.text = String.format(mainActivity.resources.getString(R.string.farm_type_template), farm.farmType)
            binding.farmTypeImageView.setImageResource(farm.farmType.getImageId(mainActivity))
            binding.farmTypeImageView.contentDescription = farm.name
            binding.root.setOnClickListener {
                val editFarmDialogFragment = EditFarmDialogFragment.newInstance(farm, position)
                editFarmDialogFragment.show(mainActivity.supportFragmentManager, EditFarmDialogFragment.TAG)
            }
        }
    }
}
