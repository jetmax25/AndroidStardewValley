package com.pickledgames.stardewvalleyguide.utils

import android.graphics.Paint
import android.os.Handler
import android.os.Looper
import android.widget.TextView

fun runWithDelay(delayMillis: Long, action: () -> Unit) {
    Handler(Looper.getMainLooper()).postDelayed({
        action()
    }, delayMillis)
}

fun TextView.underLineText() {
    paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG
}