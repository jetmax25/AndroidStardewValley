package com.pickledgames.stardewvalleyguide.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pickledgames.stardewvalleyguide.R
import com.pickledgames.stardewvalleyguide.enums.LegendaryFishingLocation
import com.pickledgames.stardewvalleyguide.models.Fish
import com.pickledgames.stardewvalleyguide.views.ImageOverlayView
import com.stfalcon.frescoimageviewer.ImageViewer
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.list_item_fish_location.*
import kotlinx.android.synthetic.main.list_item_legendary_fish_location.*

class FishLocationsAdapter(
        private val fish: Fish
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val regularFishLocations: List<String>
        get() {
            return fish.availability.locations.keys.toList()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v: View
        return if (viewType == REGULAR) {
            v = LayoutInflater.from(parent.context).inflate(R.layout.list_item_fish_location, parent, false)
            FishLocationViewHolder(v)
        } else {
            v = LayoutInflater.from(parent.context).inflate(R.layout.list_item_legendary_fish_location, parent, false)
            LegendaryFishLocationViewHolder(v, parent.context)
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
            override val containerView: View
    ) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun bindFishLocation(fishLocation: String) {
            fish_location_text_view?.text = fishLocation
        }
    }

    class LegendaryFishLocationViewHolder(
            override val containerView: View,
            private val context: Context
    ) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        @SuppressLint("DefaultLocale")
        fun bindLegendaryFishLocation(fish: Fish, position: Int) {
            val legendaryFishingLocation = LegendaryFishingLocation.fromString(fish.name)
            when (position) {
                0 -> {
                    legendary_fish_location_image_view?.setImageResource(legendaryFishingLocation.getMapImageId(context))
                    legendary_fish_location_image_view?.contentDescription = String.format(context.getString(R.string.legendary_fish_map_cd_template), fish.name)
                }
                1 -> {
                    legendary_fish_location_image_view?.setImageResource(legendaryFishingLocation.getLocationImageId(context))
                    legendary_fish_location_image_view?.contentDescription = String.format(context.getString(R.string.legendary_fish_location_cd_template), fish.name)
                }
                else -> {
                    // Failsafe
                    containerView.visibility = View.GONE
                    containerView.layoutParams = RecyclerView.LayoutParams(0, 0)
                }
            }

            containerView.setOnClickListener {
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
