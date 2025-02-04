package com.pickledgames.stardewvalleyguide.models

import android.os.Parcelable
import com.pickledgames.stardewvalleyguide.enums.Season
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Birthday(
    @Json(name = "Season") val season: Season = Season.Fall,
    @Json(name = "Day") val day: Int = 1
) : Parcelable {

    override fun toString(): String {
        return "$season $day"
    }
}
