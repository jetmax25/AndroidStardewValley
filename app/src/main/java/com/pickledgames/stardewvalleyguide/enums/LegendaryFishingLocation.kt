package com.pickledgames.stardewvalleyguide.enums

import android.content.Context
import com.pickledgames.stardewvalleyguide.utils.ImageUtil
import java.util.Locale

enum class LegendaryFishingLocation(
        val type: String
) {

    Angler("Angler"),
    CrimsonFish("Crimsonfish"),
    Glacierfish("Glacierfish"),
    Legend("Legend"),
    MutantCarp("Mutant Carp");

    fun getMapImageId(context: Context): Int {
        val misc = type.lowercase(Locale.US).replace("\\W".toRegex(), "_")
        return ImageUtil.getImageId(context, "misc_map_$misc")
    }

    fun getLocationImageId(context: Context): Int {
        val misc = type.lowercase(Locale.US).replace("\\W".toRegex(), "_")
        return ImageUtil.getImageId(context, "misc_location_$misc")
    }

    companion object {
        fun fromStringOrNull(string: String): LegendaryFishingLocation? {
            return when (string) {
                "Angler" -> Angler
                "Crimsonfish" -> CrimsonFish
                "Glacierfish" -> Glacierfish
                "Legend" -> Legend
                "Mutant Carp" -> MutantCarp
                else -> null
            }
        }

        fun fromString(string: String): LegendaryFishingLocation {
            return fromStringOrNull(string)
                    ?: throw Exception("$string is not a valid LegendaryFishingLocation")
        }
    }
}
