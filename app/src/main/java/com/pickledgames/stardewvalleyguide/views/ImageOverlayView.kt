package com.pickledgames.stardewvalleyguide.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import com.pickledgames.stardewvalleyguide.databinding.ImageOverlayViewBinding
import com.stfalcon.frescoimageviewer.ImageViewer

class ImageOverlayView : RelativeLayout {

    var imageViewer: ImageViewer? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setup()
    }

    private val binding = ImageOverlayViewBinding.inflate(LayoutInflater.from(context), this, true)
    private fun setup() {
        binding.imageOverlayExitButton.setOnClickListener {
            imageViewer?.onDismiss()
        }
    }

    fun setText(text: String) {
        binding.imageOverlayTextView.text = text
    }
}
