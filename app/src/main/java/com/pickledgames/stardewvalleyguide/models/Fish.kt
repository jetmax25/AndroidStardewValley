package com.pickledgames.stardewvalleyguide.models

import android.content.Context
import android.os.Parcelable
import com.pickledgames.stardewvalleyguide.interfaces.StardewObject
import com.pickledgames.stardewvalleyguide.utils.ImageUtil
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Fish(
        val name: String,
        val description: String,
        val commonStats: Stats,
        val silverStats: Stats,
        val goldStats: Stats,
        val availability: Availability,
        val behavior: String,
        val xp: Int,
        val difficulty: Int,
        val isLegendary: Boolean,
        val fishingLevel: Int
) : StardewObject, Parcelable {

    override fun getImageId(context: Context): Int {
        val item = name.toLowerCase().replace("\\W".toRegex(), "_")
        return ImageUtil.getImageId(context, "item_$item")
    }
}
