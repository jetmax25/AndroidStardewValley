package com.pickledgames.stardewvalleyguide.models

import android.content.Context
import com.pickledgames.stardewvalleyguide.interfaces.StardewObject

open class MuseumItem(
        val name: String,
        val description: String,
        private val collectionName: String
) : StardewObject {

    val uniqueId: String
        get() {
            return "$name$collectionName"
        }

    override fun getImageId(context: Context): Int {
        val item = name.toLowerCase().replace("\\W".toRegex(), "_")
        return context.resources.getIdentifier("item_$item", "drawable", context.packageName)
    }
}
