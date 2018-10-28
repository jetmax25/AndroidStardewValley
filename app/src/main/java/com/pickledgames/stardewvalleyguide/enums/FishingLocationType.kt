package com.pickledgames.stardewvalleyguide.enums

enum class FishingLocationType(
        val type: String
) {

    Ocean("Ocean"),
    River("River"),
    Lake("Lake"),
    Other("Other");

    companion object {
        fun fromString(string: String): FishingLocationType {
            return when {
                string.contains("Ocean", true) -> FishingLocationType.Ocean
                string.contains("River", true) -> FishingLocationType.River
                string.contains("Lake", true) -> FishingLocationType.Lake
                else -> FishingLocationType.Other
            }
        }
    }
}
