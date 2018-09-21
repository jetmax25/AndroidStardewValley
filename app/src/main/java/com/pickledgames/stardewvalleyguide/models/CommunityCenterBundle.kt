package com.pickledgames.stardewvalleyguide.models

import android.content.Context
import com.pickledgames.stardewvalleyguide.interfaces.StardewObject

data class CommunityCenterBundle(
        val name: String,
        val reward: CommunityCenterReward,
        val items: List<CommunityCenterItem>
) : StardewObject {

    override fun getImageId(context: Context): Int {
        val cleanedName = name.toLowerCase().replace("bundle", "").trim().replace("\\W".toRegex(), "_")
        return context.resources.getIdentifier("community_center_bundle_$cleanedName", "drawable", context.packageName)
    }
}
