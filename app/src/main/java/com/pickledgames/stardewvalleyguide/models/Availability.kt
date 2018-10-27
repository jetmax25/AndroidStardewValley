package com.pickledgames.stardewvalleyguide.models

import com.pickledgames.stardewvalleyguide.enums.FishingLocationType
import com.pickledgames.stardewvalleyguide.enums.Season

data class Availability(
        val weather: String,
        val locations: Map<String, FishingLocationType>,
        val seasons: Set<Season>,
        val startTime: Int,
        val endTime: Int
) {

    val timeRange: IntRange
        get() {
            return startTime until endTime
        }
}
