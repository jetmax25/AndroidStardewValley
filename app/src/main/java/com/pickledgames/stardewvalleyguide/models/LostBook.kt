package com.pickledgames.stardewvalleyguide.models

import android.content.Context
import com.pickledgames.stardewvalleyguide.R

class LostBook(
        name: String,
        description: String
) : MuseumItem(name, description, "Lost Books") {

    override fun getImageId(context: Context): Int {
        return R.drawable.misc_lost_book
    }
}
