package com.pickledgames.stardewvalleyguide.models

import android.content.Context
import com.pickledgames.stardewvalleyguide.utils.ImageUtil
import kotlinx.android.parcel.Parcelize

@Parcelize
class LostBook(
        override val name: String,
        override val description: String
) : MuseumItem(name, description, "Lost Books") {

    override fun getImageId(context: Context): Int {
        return ImageUtil.getImageId(context, "misc_lost_book")
    }
}
