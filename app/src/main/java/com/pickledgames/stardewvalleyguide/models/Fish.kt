package com.pickledgames.stardewvalleyguide.models

import android.content.Context
import com.pickledgames.stardewvalleyguide.interfaces.StardewObject

data class Fish(
        val name: String,
        val description: String,
        val commonStats: Stats,
        val silverStats: Stats,
        val goldStats: Stats,
        val availability: Availability,
        val behavior: String,
        val xp: Int,
        val difficulty: Int,
        val isLegendary: Boolean,
        val fishingLevel: Int
) : StardewObject {

    override fun getImageId(context: Context): Int {
        val item = name.toLowerCase().replace("\\W".toRegex(), "_")
        return context.resources.getIdentifier("item_$item", "drawable", context.packageName)
    }
}
