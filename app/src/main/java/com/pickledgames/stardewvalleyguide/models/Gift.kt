package com.pickledgames.stardewvalleyguide.models

import android.content.Context
import android.os.Parcelable
import com.pickledgames.stardewvalleyguide.interfaces.StardewObject
import com.pickledgames.stardewvalleyguide.utils.ImageUtil
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Gift(
        val name: String,
        val category: String
) : Parcelable, StardewObject {

    override fun getImageId(context: Context): Int {
        val item = name.toLowerCase().replace("\\W".toRegex(), "_")
        return ImageUtil.getImageId(context, "item_$item")
    }
}
