package com.pickledgames.stardewvalleyguide.models

import android.os.Parcelable
import com.pickledgames.stardewvalleyguide.enums.Season
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Birthday(
        val season: Season = Season.Fall,
        val day: Int = 1
) : Parcelable {

    override fun toString(): String {
        return "$season $day"
    }
}
