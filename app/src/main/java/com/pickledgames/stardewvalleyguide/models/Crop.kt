package com.pickledgames.stardewvalleyguide.models

import android.content.Context
import com.pickledgames.stardewvalleyguide.enums.Season
import com.pickledgames.stardewvalleyguide.interfaces.StardewObject

data class Crop(
        val name: String,
        val seeds: String?,
        val harvestTime: String,
        val goldPerDay: Double,
        val seedPrice: Int,
        val regrowthDays: Int,
        val stages: List<Int>,
        val seasons: Set<Season>,
        val commonStats: Stats,
        val silverStats: Stats,
        val goldStats: Stats
) : StardewObject {

    override fun getImageId(context: Context): Int {
        val item = name.toLowerCase().replace("\\W".toRegex(), "_")
        return context.resources.getIdentifier("item_$item", "drawable", context.packageName)
    }

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
}
