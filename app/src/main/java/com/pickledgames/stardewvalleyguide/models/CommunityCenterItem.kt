package com.pickledgames.stardewvalleyguide.models

import android.content.Context
import android.os.Parcelable
import com.pickledgames.stardewvalleyguide.enums.Season
import com.pickledgames.stardewvalleyguide.interfaces.StardewObject
import com.pickledgames.stardewvalleyguide.utils.ImageUtil
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class CommunityCenterItem(
        val name: String,
        val quantity: Int,
        val isTravelingMerchant: Boolean,
        val guide: String,
        val seasons: Set<Season>,
        val bundleName: String
) : StardewObject, Parcelable {

    val uniqueId: String
        get() {
            return "$name$bundleName"
        }

    override fun getImageId(context: Context): Int {
        val item = name.toLowerCase(Locale.US).replace("\\W".toRegex(), "_")
        val hasDigit = item.matches(".*\\d+.*".toRegex())
        return if (!hasDigit) {
            ImageUtil.getImageId(context, "item_$item")
        } else {
            ImageUtil.getImageId(context, "item_gold")
        }
    }
}
