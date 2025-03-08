package com.pickledgames.stardewvalleyguide.utils

import android.content.Context
import android.util.Log
import com.pickledgames.stardewvalleyguide.R

object ImageUtil {

    private val TAG = ImageUtil::class.java.simpleName

    fun getImageId(context: Context, resourceName: String): Int {
        val imageId = context.resources.getIdentifier(resourceName, "drawable", context.packageName)

        return if (imageId == 0) {
            Log.e(TAG, "Image resource $resourceName not found. Using default image.")
            R.drawable.default_image
        } else {
            imageId
        }
    }
}
