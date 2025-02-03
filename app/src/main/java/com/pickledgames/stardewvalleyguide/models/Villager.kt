package com.pickledgames.stardewvalleyguide.models

import android.content.Context
import android.os.Parcelable
import com.pickledgames.stardewvalleyguide.interfaces.StardewObject
import com.pickledgames.stardewvalleyguide.utils.ImageUtil
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Villager(
    @Transient val name: String = "",
    @Json(name = "Birthday") val birthday: Birthday = Birthday(),
    @Json(name = "CanMarry")  val canMarry: Boolean = false,
    @Json(name = "CanGift") val canGift: Boolean = false,
    @Json(name = "Events") val events: List<Event> = emptyList()
) : Parcelable, StardewObject {

    override fun getImageId(context: Context): Int {
        return ImageUtil.getImageId(context, "villager_${name.toLowerCase(Locale.US)}")
    }
}
