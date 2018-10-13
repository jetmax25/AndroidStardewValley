package com.pickledgames.stardewvalleyguide.models

import android.content.Context
import com.pickledgames.stardewvalleyguide.interfaces.StardewObject

data class CommunityCenterBundle(
        val name: String,
        val reward: CommunityCenterReward,
        val items: List<CommunityCenterItem>,
        val needed: Int
) : StardewObject {

    override fun getImageId(context: Context): Int {
        val cleanedName = name.toLowerCase().replace("bundle", "").trim().replace("\\W".toRegex(), "_")
        return context.resources.getIdentifier("community_center_bundle_$cleanedName", "drawable", context.packageName)
    }

    override fun toString(): String {
        return name
    }
}
