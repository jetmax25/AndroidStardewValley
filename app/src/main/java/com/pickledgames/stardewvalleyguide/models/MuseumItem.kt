package com.pickledgames.stardewvalleyguide.models

import android.content.Context
import android.os.Parcelable
import com.pickledgames.stardewvalleyguide.interfaces.StardewObject
import kotlinx.android.parcel.Parcelize

@Parcelize
open class MuseumItem(
        open val name: String,
        open val description: String,
        private val collectionName: String
) : StardewObject, Parcelable {

    val uniqueId: String
        get() {
            return "$name$collectionName"
        }

    override fun getImageId(context: Context): Int {
        val item = name.toLowerCase().replace("\\W".toRegex(), "_")
        return context.resources.getIdentifier("item_$item", "drawable", context.packageName)
    }
}