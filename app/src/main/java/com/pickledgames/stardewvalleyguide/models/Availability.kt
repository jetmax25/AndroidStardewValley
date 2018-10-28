package com.pickledgames.stardewvalleyguide.models

import android.os.Parcelable
import com.pickledgames.stardewvalleyguide.enums.FishingLocationType
import com.pickledgames.stardewvalleyguide.enums.Season
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Availability(
        val weather: String,
        val locations: Map<String, FishingLocationType>,
        val seasons: Set<Season>,
        val startTime: Int,
        val endTime: Int
) : Parcelable {

    val timeRange: IntRange
        get() {
            return startTime until endTime
        }
}
