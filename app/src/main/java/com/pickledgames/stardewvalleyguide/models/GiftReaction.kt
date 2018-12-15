package com.pickledgames.stardewvalleyguide.models

import android.content.Context
import com.pickledgames.stardewvalleyguide.enums.Reaction
import com.pickledgames.stardewvalleyguide.interfaces.StardewObject
import com.pickledgames.stardewvalleyguide.utils.ImageUtil

data class GiftReaction(
        val reaction: Reaction,
        val villagerName: String,
        val itemName: String,
        val category: String
) : StardewObject {

    override fun getImageId(context: Context): Int {
        val item = itemName.toLowerCase().replace("\\W".toRegex(), "_")
        return ImageUtil.getImageId(context, "item_$item")
    }
}
