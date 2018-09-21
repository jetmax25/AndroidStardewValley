package com.pickledgames.stardewvalleyguide.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pickledgames.stardewvalleyguide.R
import com.pickledgames.stardewvalleyguide.activities.MainActivity
import com.pickledgames.stardewvalleyguide.fragments.EditFarmDialogFragment
import com.pickledgames.stardewvalleyguide.models.Farm
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.list_item_farm.*

class FarmsAdapter(
        private val farms: MutableList<Farm>,
        private val mainActivity: MainActivity
) : RecyclerView.Adapter<FarmsAdapter.FarmViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FarmViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.list_item_farm, parent, false)
        return FarmViewHolder(v, mainActivity)
    }

    override fun getItemCount(): Int {
        return farms.size
    }

    override fun onBindViewHolder(holder: FarmViewHolder, position: Int) {
        holder.bindFarm(farms[position], position)
    }

    class FarmViewHolder(
            override val containerView: View,
            private val mainActivity: MainActivity
    ) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun bindFarm(farm: Farm, position: Int) {
            farm_name_text_view.text = String.format(mainActivity.resources.getString(R.string.farm_name_template), farm.name)
            farm_type_text_view.text = String.format(mainActivity.resources.getString(R.string.farm_type_template), farm.farmType)
            farm_type_image_view.setImageResource(farm.farmType.getImageId(mainActivity))
            farm_type_image_view.contentDescription = farm.name
            containerView.setOnClickListener {
                val editFarmDialogFragment = EditFarmDialogFragment.newInstance(farm, position)
                editFarmDialogFragment.show(mainActivity.supportFragmentManager, EditFarmDialogFragment.TAG)
            }
        }
    }
}
