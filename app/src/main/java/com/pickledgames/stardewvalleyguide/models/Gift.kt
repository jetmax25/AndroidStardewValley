package com.pickledgames.stardewvalleyguide.models

import android.content.Context
import android.os.Parcelable
import com.pickledgames.stardewvalleyguide.interfaces.StardewObject
import com.pickledgames.stardewvalleyguide.utils.ImageUtil
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Gift(
        val name: String,
        val category: String
) : Parcelable, StardewObject {

    override fun getImageId(context: Context): Int {
        val item = name.toLowerCase(Locale.US).replace("\\W".toRegex(), "_")
        return ImageUtil.getImageId(context, "item_$item")
    }
}
