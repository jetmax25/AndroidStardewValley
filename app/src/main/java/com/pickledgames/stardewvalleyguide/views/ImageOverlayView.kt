package com.pickledgames.stardewvalleyguide.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import com.pickledgames.stardewvalleyguide.R
import com.stfalcon.frescoimageviewer.ImageViewer
import kotlinx.android.synthetic.main.image_overlay_view.view.*

class ImageOverlayView : RelativeLayout {

    var imageViewer: ImageViewer? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setup()
    }

    private fun setup() {
        LayoutInflater.from(context).inflate(R.layout.image_overlay_view, this, true)
        image_overlay_exit_button.setOnClickListener {
            imageViewer?.onDismiss()
        }
    }

    fun setText(text: String) {
        image_overlay_text_view.text = text
    }
}
