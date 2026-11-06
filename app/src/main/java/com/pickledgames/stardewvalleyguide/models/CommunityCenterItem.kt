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
        return ImageUtil.getImageId(context, communityCenterItemResource(name))
    }
}

/**
 * Drawable for a community center bundle item.
 *
 * Quality notes are part of the requirement, not a separate sprite:
 * "Wine Silver Quality" uses item_wine, and "Ancient Fruit (Gold Quality)"
 * uses item_ancient_fruit. Names that still contain a digit after that
 * ("2,500g") keep the gold icon. "Parsnip High Quality" is unchanged
 * because those drawables already exist.
 *
 * Keep in sync with community_center_item_resource in scripts/check_item_images.py.
 */
internal fun communityCenterItemResource(displayName: String): String {
    val withoutParenthetical = displayName.replace("\\s*\\([^)]*\\)".toRegex(), " ")
    val withoutQuality = withoutParenthetical.replace(
            "(?i)\\s+(?:silver|gold|iridium)\\s+quality\\s*$".toRegex(),
            ""
    )
    val item = withoutQuality.trim()
            .replace("\\s+".toRegex(), " ")
            .toLowerCase(Locale.US)
            .replace("\\W".toRegex(), "_")
    return if (item.matches(".*\\d+.*".toRegex())) "item_gold" else "item_$item"
}
