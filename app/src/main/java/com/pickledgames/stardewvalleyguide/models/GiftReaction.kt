package com.pickledgames.stardewvalleyguide.models

import android.content.Context
import com.pickledgames.stardewvalleyguide.enums.Reaction
import com.pickledgames.stardewvalleyguide.interfaces.StardewObject

data class GiftReaction(
        val reaction: Reaction,
        val villagerName: String,
        val itemName: String,
        val category: String
) : StardewObject {

    override fun getImageId(context: Context): Int {
        val item = itemName.toLowerCase().replace("\\W".toRegex(), "_")
        val identifier = context.resources.getIdentifier("item_$item", "drawable", context.packageName)
        if (identifier == 0) throw Exception("Resource item_$item not found")
        return identifier
    }
}
