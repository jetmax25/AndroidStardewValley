package com.pickledgames.stardewvalleyguide.models

import android.content.Context
import com.pickledgames.stardewvalleyguide.interfaces.StardewObject

data class CommunityCenterItem(
        val name: String,
        val quantity: Int,
        val isTravelingMerchant: Boolean,
        val guides: List<String>
) : StardewObject {

    override fun getImageId(context: Context): Int {
        val item = name.toLowerCase().replace("\\W".toRegex(), "_")
        val hasDigit = item.matches(".*\\d+.*".toRegex())
        val identifier = if (!hasDigit) {
            context.resources.getIdentifier("item_$item", "drawable", context.packageName)
        } else {
            context.resources.getIdentifier("item_gold", "drawable", context.packageName)
        }
        if (identifier == 0) throw Exception("Resource item_$item not found")
        return identifier
    }
}
