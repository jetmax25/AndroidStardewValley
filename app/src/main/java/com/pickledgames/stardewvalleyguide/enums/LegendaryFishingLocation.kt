package com.pickledgames.stardewvalleyguide.enums

import android.content.Context
import java.util.*

enum class LegendaryFishingLocation(
        val type: String
) {

    Angler("Angler"),
    CrimsonFish("Crimsonfish"),
    Glacierfish("Glacierfish"),
    Legend("Legend"),
    MutantCarp("Mutant Carp");

    fun getMapImageId(context: Context): Int {
        val misc = type.toLowerCase(Locale.US).replace("\\W".toRegex(), "_")
        return context.resources.getIdentifier("misc_map_$misc", "drawable", context.packageName)
    }

    fun getLocationImageId(context: Context): Int {
        val misc = type.toLowerCase(Locale.US).replace("\\W".toRegex(), "_")
        return context.resources.getIdentifier("misc_location_$misc", "drawable", context.packageName)
    }

    companion object {
        fun fromString(string: String): LegendaryFishingLocation {
            return when (string) {
                "Angler" -> Angler
                "Crimsonfish" -> CrimsonFish
                "Glacierfish" -> Glacierfish
                "Legend" -> Legend
                "Mutant Carp" -> MutantCarp
                else -> throw Exception("$string is not a valid LegendaryFishingLocation")
            }
        }
    }
}
