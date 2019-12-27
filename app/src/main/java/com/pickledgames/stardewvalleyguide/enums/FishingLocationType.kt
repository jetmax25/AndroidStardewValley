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
                string.contains("Ocean", true) -> Ocean
                string.contains("River", true) -> River
                string.contains("Lake", true) -> Lake
                else -> Other
            }
        }
    }
}
