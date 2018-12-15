package com.pickledgames.stardewvalleyguide.models

import android.content.Context
import android.os.Parcelable
import com.pickledgames.stardewvalleyguide.interfaces.StardewObject
import com.pickledgames.stardewvalleyguide.utils.ImageUtil
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Villager(
        val name: String,
        val birthday: Birthday = Birthday(),
        val canMarry: Boolean = false
) : Parcelable, StardewObject {

    override fun getImageId(context: Context): Int {
        return ImageUtil.getImageId(context, "villager_${name.toLowerCase()}")
    }
}
