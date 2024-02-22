package com.pickledgames.stardewvalleyguide.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pickledgames.stardewvalleyguide.R
import com.pickledgames.stardewvalleyguide.databinding.ListItemCropStageBinding
import com.pickledgames.stardewvalleyguide.models.Crop

class CropStagesAdapter(
        private val crop: Crop
) : RecyclerView.Adapter<CropStagesAdapter.CropStageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CropStageViewHolder {
        val binding = ListItemCropStageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CropStageViewHolder(binding, parent.context)
    }

    override fun getItemCount(): Int {
        return if (crop.regrowthDays > 0) crop.stages.size + 1 else crop.stages.size
    }

    override fun onBindViewHolder(holder: CropStageViewHolder, position: Int) {
        holder.bindCropStage(crop, position)
    }

    class CropStageViewHolder(
            private val binding: ListItemCropStageBinding,
            val context: Context
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindCropStage(crop: Crop, position: Int) {
            // Regrowth
            if (position == crop.stages.size) {
                val regrowthText = context.resources.getQuantityString(R.plurals.regrowth_stage_template, crop.regrowthDays, crop.regrowthDays)
                binding.cropStageTextView.text = regrowthText
                binding.cropStageImageView.setImageResource(crop.getRegrowthImageId(context))
                binding.cropStageImageView.contentDescription = regrowthText
                return
            }

            val stageText = when (position) {
                0 -> context.getString(R.string.planting)
                in 1 until crop.stages.size - 1 -> String.format(context.getString(R.string.day_stage_template), crop.stages[position])
                crop.stages.size - 1 -> context.resources.getQuantityString(R.plurals.harvest_stage_template, crop.harvestTime, crop.harvestTime)
                else -> throw Exception("$position is not a valid crop stage position for ${crop.name}")
            }

            binding.cropStageTextView.text = stageText
            binding.cropStageImageView.setImageResource(crop.getStageImageId(position, context))
            binding.cropStageImageView.contentDescription = stageText
        }
    }
}
