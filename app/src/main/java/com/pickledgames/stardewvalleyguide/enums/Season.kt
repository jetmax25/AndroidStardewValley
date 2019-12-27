package com.pickledgames.stardewvalleyguide.enums

import android.content.Context
import com.pickledgames.stardewvalleyguide.interfaces.StardewObject
import com.pickledgames.stardewvalleyguide.utils.ImageUtil
import java.util.*

enum class Season(
        val type: String
) : StardewObject {

    Fall("Fall"),
    Winter("Winter"),
    Spring("Spring"),
    Summer("Summer");

    override fun getImageId(context: Context): Int {
        return ImageUtil.getImageId(context, "season_${type.toLowerCase(Locale.US)}")
    }

    companion object {
        fun fromString(string: String): Season {
            return when (string) {
                "Fall" -> Fall
                "Winter" -> Winter
                "Spring" -> Spring
                "Summer" -> Summer
                else -> throw Exception("$string is not a valid Season")
            }
        }
    }
}
