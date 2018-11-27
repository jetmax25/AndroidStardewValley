package com.pickledgames.stardewvalleyguide.models

import android.content.Context
import android.os.Parcelable
import com.pickledgames.stardewvalleyguide.enums.Season
import com.pickledgames.stardewvalleyguide.interfaces.StardewObject
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Crop(
        val name: String,
        val seeds: String?,
        val harvestTime: Int,
        val goldPerDay: Double,
        val seedPrice: Int,
        val regrowthDays: Int,
        val stages: List<Int>,
        val seasons: Set<Season>,
        val commonStats: Stats,
        val silverStats: Stats,
        val goldStats: Stats
) : StardewObject, Parcelable {

    override fun getImageId(context: Context): Int {
        val item = name.toLowerCase().replace("\\W".toRegex(), "_")
        return context.resources.getIdentifier("item_$item", "drawable", context.packageName)
    }

    // If seed name exists use that, otherwise use name appended with _seeds, as a final fallback
    // just use crop image id
    fun getSeedImageId(context: Context): Int {
        return if (seeds != null) {
            val item = seeds.toLowerCase().replace("\\W".toRegex(), "_")
            context.resources.getIdentifier("item_$item", "drawable", context.packageName)
        } else {
            val item = name.toLowerCase().replace("\\W".toRegex(), "_")
            val seedResource = context.resources.getIdentifier("item_${item}_seeds", "drawable", context.packageName)
            if (seedResource == 0) getImageId(context) else seedResource
        }
    }

    // Position is 0 indexed
    fun getStageImageId(position: Int, context: Context): Int {
        if (position < 0 || position >= stages.size) throw Exception("$position is not a valid crop stage position for $name")
        val item = name.toLowerCase().replace("\\W".toRegex(), "_")
        return context.resources.getIdentifier("item_${item}_phase_${position + 1}", "drawable", context.packageName)
    }

    fun getRegrowthImageId(context: Context): Int {
        val item = name.toLowerCase().replace("\\W".toRegex(), "_")
        return context.resources.getIdentifier("item_${item}_regrowth", "drawable", context.packageName)
    }
}
