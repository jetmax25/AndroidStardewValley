package com.pickledgames.stardewvalleyguide.models

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Event(
    @Json(name = "Hearts") val hearts: Int,
    @Json(name = "Trigger") val trigger: String,
    @Json(name = "Details") val details: String,
    @Json(name = "Choices") val choices: List<Choice> = emptyList()
) : Parcelable

@Parcelize
data class Choice(
    @Json(name = "Option") val option: String,
    @Json(name = "Reaction") val reaction: Int
) : Parcelable
