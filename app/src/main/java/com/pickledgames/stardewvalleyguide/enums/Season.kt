package com.pickledgames.stardewvalleyguide.enums

import android.content.Context
import com.pickledgames.stardewvalleyguide.interfaces.StardewObject
import com.pickledgames.stardewvalleyguide.utils.ImageUtil

enum class Season(
        val type: String
) : StardewObject {

    Fall("Fall"),
    Winter("Winter"),
    Spring("Spring"),
    Summer("Summer");

    override fun getImageId(context: Context): Int {
        return ImageUtil.getImageId(context, "season_${type.toLowerCase()}")
    }

    companion object {
        fun fromString(string: String): Season {
            return when (string) {
                "Fall" -> Season.Fall
                "Winter" -> Season.Winter
                "Spring" -> Season.Spring
                "Summer" -> Season.Summer
                else -> throw Exception("$string is not a valid Season")
            }
        }
    }
}
