package com.pickledgames.stardewvalleyguide.enums

import android.content.Context
import com.pickledgames.stardewvalleyguide.interfaces.StardewObject

enum class FarmType(
        val type: String
) : StardewObject {

    Standard("Standard"),
    Riverland("Riverland"),
    Forest("Forest"),
    Hilltop("Hilltop"),
    Wilderness("Wilderness");

    override fun getImageId(context: Context): Int {
        return context.resources.getIdentifier("farm_type_${type.toLowerCase()}", "drawable", context.packageName)
    }
}
