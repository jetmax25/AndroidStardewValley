package com.pickledgames.stardewvalleyguide.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pickledgames.stardewvalleyguide.R
import com.pickledgames.stardewvalleyguide.databinding.ListItemFishLocationBinding
import com.pickledgames.stardewvalleyguide.databinding.ListItemLegendaryFishLocationBinding
import com.pickledgames.stardewvalleyguide.enums.LegendaryFishingLocation
import com.pickledgames.stardewvalleyguide.models.Fish
import com.pickledgames.stardewvalleyguide.views.ImageOverlayView
import com.stfalcon.frescoimageviewer.ImageViewer

class FishLocationsAdapter(
        private val fish: Fish
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val regularFishLocations: List<String>
        get() {
            return fish.availability.locations.keys.toList()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == REGULAR) {
            val binding = ListItemFishLocationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            FishLocationViewHolder(binding)
        } else {
            val binding = ListItemLegendaryFishLocationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            LegendaryFishLocationViewHolder(binding, parent.context)
        }
    }

    override fun getItemCount(): Int {
        return if (fish.isLegendary) 2 else fish.availability.locations.keys.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (fish.isLegendary) LEGENDARY else REGULAR
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is FishLocationViewHolder) {
            holder.bindFishLocation(regularFishLocations[position])
        } else if (holder is LegendaryFishLocationViewHolder) {
            holder.bindLegendaryFishLocation(fish, position)
        }
    }

    companion object {
        const val REGULAR = 0
        const val LEGENDARY = 1
    }

    class FishLocationViewHolder(
            private val binding: ListItemFishLocationBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindFishLocation(fishLocation: String) {
            binding.fishLocationTextView.text = fishLocation
        }
    }

    class LegendaryFishLocationViewHolder(
            private val binding: ListItemLegendaryFishLocationBinding,
            private val context: Context
    ) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("DefaultLocale")
        fun bindLegendaryFishLocation(fish: Fish, position: Int) {
            val legendaryFishingLocation = LegendaryFishingLocation.fromString(fish.name)
            when (position) {
                0 -> {
                    binding.legendaryFishLocationImageView.setImageResource(legendaryFishingLocation.getMapImageId(context))
                    binding.legendaryFishLocationImageView.contentDescription = String.format(context.getString(R.string.legendary_fish_map_cd_template), fish.name)
                }
                1 -> {
                    binding.legendaryFishLocationImageView.setImageResource(legendaryFishingLocation.getLocationImageId(context))
                    binding.legendaryFishLocationImageView.contentDescription = String.format(context.getString(R.string.legendary_fish_location_cd_template), fish.name)
                }
                else -> {
                    // Failsafe
                    binding.root.visibility = View.GONE
                    binding.root.layoutParams = RecyclerView.LayoutParams(0, 0)
                }
            }

            binding.root.setOnClickListener {
                val imageUrls = listOf<Uri>(
                        Uri.parse("res:///${legendaryFishingLocation.getMapImageId(context)}"),
                        Uri.parse("res:///${legendaryFishingLocation.getLocationImageId(context)}")
                )

                val overlayView = ImageOverlayView(context)
                val imageViewer = ImageViewer.Builder(context, imageUrls)
                        .setStartPosition(position)
                        .setOverlayView(overlayView)
                        .setImageChangeListener { position ->
                            val text = if (position == 0) {
                                "${fish.name.capitalize()} Map"
                            } else {
                                "${fish.name.capitalize()} Location"
                            }

                            overlayView.setText(text)
                        }
                        .show()

                overlayView.imageViewer = imageViewer
            }
        }
    }
}
