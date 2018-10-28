package com.pickledgames.stardewvalleyguide.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Stats(
        val health: Int,
        val energy: Int,
        val price: Int
) : Parcelable
