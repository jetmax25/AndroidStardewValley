package com.pickledgames.stardewvalleyguide.enums

import android.content.Context
import com.pickledgames.stardewvalleyguide.interfaces.StardewObject
import com.pickledgames.stardewvalleyguide.utils.ImageUtil
import java.util.*

enum class FarmType(
        val type: String
) : StardewObject {

    Standard("Standard"),
    Riverland("Riverland"),
    Forest("Forest"),
    Hilltop("Hilltop"),
    Wilderness("Wilderness");

    override fun getImageId(context: Context): Int {
        return ImageUtil.getImageId(context, "farm_type_${type.toLowerCase(Locale.US)}")
    }
}
