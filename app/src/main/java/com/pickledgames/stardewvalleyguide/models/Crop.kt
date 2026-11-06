package com.pickledgames.stardewvalleyguide.models

import android.content.Context
import android.os.Parcelable
import com.pickledgames.stardewvalleyguide.enums.Season
import com.pickledgames.stardewvalleyguide.interfaces.StardewObject
import com.pickledgames.stardewvalleyguide.utils.AssetNames
import com.pickledgames.stardewvalleyguide.utils.ImageUtil
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
        return ImageUtil.getImageId(context, AssetNames.itemDrawable(name))
    }

    // If seed name exists use that, otherwise use name appended with _seeds, as a final fallback
    // just use crop image id
    fun getSeedImageId(context: Context): Int {
        for (candidate in AssetNames.cropSeedDrawables(name, seeds)) {
            val seedResource = context.resources.getIdentifier(candidate, "drawable", context.packageName)
            if (seedResource != 0) return seedResource
        }
        return getImageId(context)
    }

    // Position is 0 indexed
    fun getStageImageId(position: Int, context: Context): Int {
        if (position < 0 || position >= stages.size) throw Exception("$position is not a valid crop stage position for $name")
        return ImageUtil.getImageId(
                context,
                AssetNames.cropPhaseDrawable(name, position + 1)
        )
    }

    fun getRegrowthImageId(context: Context): Int {
        return ImageUtil.getImageId(
                context,
                AssetNames.cropRegrowthDrawable(name)
        )
    }
}
