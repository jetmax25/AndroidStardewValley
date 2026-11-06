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
            // Overnight windows are stored with an end earlier than the start (for example 22 to 2).
            // Shift the end into the next day so filters can intersect the range.
            val exclusiveEnd = if (endTime > startTime) endTime else endTime + 24
            return startTime until exclusiveEnd
        }
}
