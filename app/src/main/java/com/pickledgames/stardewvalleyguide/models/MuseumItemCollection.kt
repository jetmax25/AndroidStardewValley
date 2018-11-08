package com.pickledgames.stardewvalleyguide.models

import android.content.Context
import com.pickledgames.stardewvalleyguide.R
import com.pickledgames.stardewvalleyguide.interfaces.StardewObject

data class MuseumItemCollection(
        val name: String,
        val numberOfItems: Int
) : StardewObject {

    override fun getImageId(context: Context): Int {
        return when (name) {
            "Minerals" -> R.drawable.item_diamond
            "Artifacts" -> R.drawable.misc_artifact
            "Lost Books" -> R.drawable.misc_lost_book
            else -> throw Exception("$name is not a valid MuseumItemCollection")
        }
    }
}
