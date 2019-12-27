package com.pickledgames.stardewvalleyguide.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pickledgames.stardewvalleyguide.R
import com.pickledgames.stardewvalleyguide.activities.MainActivity
import com.pickledgames.stardewvalleyguide.fragments.CropFragment
import com.pickledgames.stardewvalleyguide.models.Crop
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.list_item_crop.*

class CropsAdapter(
        private var crops: List<Crop>,
        private val mainActivity: MainActivity
) : RecyclerView.Adapter<CropsAdapter.CropViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CropViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.list_item_crop, parent, false)
        return CropViewHolder(v, mainActivity)
    }

    override fun getItemCount(): Int {
        return crops.size
    }

    override fun onBindViewHolder(holder: CropViewHolder, position: Int) {
        holder.bindCrop(crops[position])
    }

    fun updateCrops(c: List<Crop>) {
        crops = c
        notifyDataSetChanged()
    }

    class CropViewHolder(
            override val containerView: View,
            private val mainActivity: MainActivity
    ) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun bindCrop(crop: Crop) {
            crop_image_view?.setImageResource(crop.getImageId(mainActivity))
            crop_image_view?.contentDescription = crop.name
            crop_text_view?.text = crop.name
            crop_seed_price_image_view?.setImageResource(crop.getSeedImageId(mainActivity))
            crop_seed_price_image_view?.contentDescription = crop.name
            crop_seed_price_text_view?.text = crop.seedPrice.toString()
            crop_gold_text_view?.text = crop.commonStats.price.toString()
            crop_harvest_time_text_view?.text = crop.harvestTime.toString()
            containerView.setOnClickListener {
                mainActivity.pushFragment(CropFragment.newInstance(crop))
            }
        }
    }
}
