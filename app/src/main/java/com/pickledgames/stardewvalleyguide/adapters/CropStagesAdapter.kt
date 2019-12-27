package com.pickledgames.stardewvalleyguide.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pickledgames.stardewvalleyguide.R
import com.pickledgames.stardewvalleyguide.models.Crop
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.list_item_crop_stage.*

class CropStagesAdapter(
        private val crop: Crop
) : RecyclerView.Adapter<CropStagesAdapter.CropStageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CropStageViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.list_item_crop_stage, parent, false)
        return CropStageViewHolder(v, parent.context)
    }

    override fun getItemCount(): Int {
        return if (crop.regrowthDays > 0) crop.stages.size + 1 else crop.stages.size
    }

    override fun onBindViewHolder(holder: CropStageViewHolder, position: Int) {
        holder.bindCropStage(crop, position)
    }

    class CropStageViewHolder(
            override val containerView: View,
            val context: Context
    ) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun bindCropStage(crop: Crop, position: Int) {
            // Regrowth
            if (position == crop.stages.size) {
                val regrowthText = context.resources.getQuantityString(R.plurals.regrowth_stage_template, crop.regrowthDays, crop.regrowthDays)
                crop_stage_text_view?.text = regrowthText
                crop_stage_image_view?.setImageResource(crop.getRegrowthImageId(context))
                crop_stage_image_view?.contentDescription = regrowthText
                return
            }

            val stageText = when (position) {
                0 -> context.getString(R.string.planting)
                in 1 until crop.stages.size - 1 -> String.format(context.getString(R.string.day_stage_template), crop.stages[position])
                crop.stages.size - 1 -> context.resources.getQuantityString(R.plurals.harvest_stage_template, crop.harvestTime, crop.harvestTime)
                else -> throw Exception("$position is not a valid crop stage position for ${crop.name}")
            }

            crop_stage_text_view?.text = stageText
            crop_stage_image_view?.setImageResource(crop.getStageImageId(position, context))
            crop_stage_image_view?.contentDescription = stageText
        }
    }
}
