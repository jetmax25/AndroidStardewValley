package com.pickledgames.stardewvalleyguide.models

import android.content.Context
import com.pickledgames.stardewvalleyguide.interfaces.StardewObject
import com.pickledgames.stardewvalleyguide.utils.ImageUtil
import java.util.*

data class CommunityCenterBundle(
        val name: String,
        val reward: CommunityCenterReward,
        val items: List<CommunityCenterItem>,
        val needed: Int
) : StardewObject {

    override fun getImageId(context: Context): Int {
        val cleanedName = name.toLowerCase(Locale.US).replace("bundle", "").trim().replace("\\W".toRegex(), "_")
        return ImageUtil.getImageId(context, "community_center_bundle_$cleanedName")
    }

    override fun toString(): String {
        return name
    }
}
