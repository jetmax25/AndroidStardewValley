package com.pickledgames.stardewvalleyguide.models

import com.pickledgames.stardewvalleyguide.enums.Season

data class Availability(
        val weather: String,
        val locations: List<String>,
        val seasons: Set<Season>,
        val startTime: Int,
        val endTime: Int
)
