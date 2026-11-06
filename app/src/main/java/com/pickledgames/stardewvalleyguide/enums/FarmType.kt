package com.pickledgames.stardewvalleyguide.enums

import android.content.Context
import com.pickledgames.stardewvalleyguide.interfaces.StardewObject
import com.pickledgames.stardewvalleyguide.utils.AssetNames
import com.pickledgames.stardewvalleyguide.utils.ImageUtil

enum class FarmType(
        val type: String
) : StardewObject {

    Standard("Standard"),
    Riverland("Riverland"),
    Forest("Forest"),
    Hilltop("Hilltop"),
    Wilderness("Wilderness"),
    FourCorners("Four Corners"),
    Beach("Beach"),
    Meadowlands("Meadowlands");

    override fun getImageId(context: Context): Int {
        return ImageUtil.getImageId(context, AssetNames.farmTypeDrawable(type))
    }

    override fun toString(): String = type
}
