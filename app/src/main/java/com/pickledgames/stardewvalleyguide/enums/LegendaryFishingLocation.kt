package com.pickledgames.stardewvalleyguide.enums

import android.content.Context

enum class LegendaryFishingLocation(
        val type: String
) {

    Angler("Angler"),
    CrimsonFish("Crimsonfish"),
    Glacierfish("Glacierfish"),
    Legend("Legend"),
    MutantCarp("Mutant Carp");

    fun getMapImageId(context: Context): Int {
        val misc = type.toLowerCase().replace("\\W".toRegex(), "_")
        return context.resources.getIdentifier("misc_map_$misc", "drawable", context.packageName)
    }

    fun getLocationImageId(context: Context): Int {
        val misc = type.toLowerCase().replace("\\W".toRegex(), "_")
        return context.resources.getIdentifier("misc_location_$misc", "drawable", context.packageName)
    }

    companion object {
        fun fromString(string: String): LegendaryFishingLocation {
            return when (string) {
                "Angler" -> LegendaryFishingLocation.Angler
                "Crimsonfish" -> LegendaryFishingLocation.CrimsonFish
                "Glacierfish" -> LegendaryFishingLocation.Glacierfish
                "Legend" -> LegendaryFishingLocation.Legend
                "Mutant Carp" -> LegendaryFishingLocation.MutantCarp
                else -> throw Exception("$string is not a valid LegendaryFishingLocation")
            }
        }
    }
}
