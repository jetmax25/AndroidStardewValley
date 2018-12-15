package com.pickledgames.stardewvalleyguide.utils

import android.content.Context
import android.util.Log
import com.crashlytics.android.Crashlytics

object ImageUtil {

    private val TAG = ImageUtil::class.java.simpleName

    fun getImageId(context: Context, resourceName: String): Int {
        val imageId = context.resources.getIdentifier(resourceName, "drawable", context.packageName)
        if (imageId == 0) {
            val errorMessage = "Image resource $resourceName not found."
            Log.e(TAG, errorMessage)
            Crashlytics.logException(Exception(errorMessage))
        }

        return imageId
    }
}
