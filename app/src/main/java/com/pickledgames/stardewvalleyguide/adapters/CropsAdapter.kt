package com.pickledgames.stardewvalleyguide.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pickledgames.stardewvalleyguide.activities.MainActivity
import com.pickledgames.stardewvalleyguide.databinding.ListItemCropBinding
import com.pickledgames.stardewvalleyguide.fragments.CropFragment
import com.pickledgames.stardewvalleyguide.models.Crop

class CropsAdapter(
        private var crops: List<Crop>,
        private val mainActivity: MainActivity
) : RecyclerView.Adapter<CropsAdapter.CropViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CropViewHolder {
        val binding = ListItemCropBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CropViewHolder(binding, mainActivity)
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
            private val binding: ListItemCropBinding,
            private val mainActivity: MainActivity
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindCrop(crop: Crop) {
            binding.cropImageView.setImageResource(crop.getImageId(mainActivity))
            binding.cropImageView.contentDescription = crop.name
            binding.cropTextView.text = crop.name
            binding.cropSeedPriceImageView.setImageResource(crop.getSeedImageId(mainActivity))
            binding.cropSeedPriceImageView.contentDescription = crop.name
            binding.cropSeedPriceTextView.text = crop.seedPrice.toString()
            binding.cropGoldTextView.text = crop.commonStats.price.toString()
            binding.cropHarvestTimeTextView.text = crop.harvestTime.toString()
            binding.root.setOnClickListener {
                mainActivity.pushFragment(CropFragment.newInstance(crop))
            }
        }
    }
}
