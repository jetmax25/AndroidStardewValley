package com.pickledgames.stardewvalleyguide.models

import android.content.Context
import com.pickledgames.stardewvalleyguide.interfaces.StardewObject
import com.pickledgames.stardewvalleyguide.utils.AssetNames
import com.pickledgames.stardewvalleyguide.utils.ImageUtil

data class CommunityCenterBundle(
        val name: String,
        val reward: CommunityCenterReward,
        val items: List<CommunityCenterItem>,
        val needed: Int
) : StardewObject {

    override fun getImageId(context: Context): Int {
        return ImageUtil.getImageId(context, AssetNames.bundleDrawable(name))
    }

    override fun toString(): String {
        return name
    }
}
