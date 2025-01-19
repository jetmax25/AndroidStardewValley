package com.pickledgames.stardewvalleyguide.utils

import android.os.Handler
import android.os.Looper

fun runWithDelay(delayMillis: Long, action: () -> Unit) {
    Handler(Looper.getMainLooper()).postDelayed({
        action()
    }, delayMillis)
}