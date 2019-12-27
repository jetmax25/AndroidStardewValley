package com.pickledgames.stardewvalleyguide.models

import android.content.Context
import com.pickledgames.stardewvalleyguide.R
import com.pickledgames.stardewvalleyguide.interfaces.StardewObject
import com.pickledgames.stardewvalleyguide.utils.ImageUtil
import java.util.*

data class MuseumItemCollection(
        val name: String,
        val numberOfItems: Int
) : StardewObject {

    override fun getImageId(context: Context): Int {
        return when (name) {
            "Minerals" -> R.drawable.item_diamond
            "Artifacts" -> R.drawable.misc_artifact
            "Lost Books" -> R.drawable.misc_lost_book
            else -> ImageUtil.getImageId(context, "item_${name.toLowerCase(Locale.US)}")
        }
    }
}
