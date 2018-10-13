package com.pickledgames.stardewvalleyguide.views

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.RelativeLayout
import com.pickledgames.stardewvalleyguide.R
import kotlinx.android.synthetic.main.sign_text_view.view.*

class SignTextView : RelativeLayout {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setup(attrs)
    }

    private fun setup(attrs: AttributeSet?) {
        LayoutInflater.from(context).inflate(R.layout.sign_text_view, this, true)
        context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.SignTextView,
                0,
                0
        ).apply {
            try {
                val text = getText(R.styleable.SignTextView_text) ?: ""
                text_view.text = text

                val textSize = getDimension(R.styleable.SignTextView_textSize, text_view.textSize)
                text_view.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)

                val containerMarginLayoutParams = sign_header_container.layoutParams as MarginLayoutParams
                val margin = getDimensionPixelSize(R.styleable.SignTextView_margin, 0)
                containerMarginLayoutParams.setMargins(margin, margin, margin, margin)

                val padding = getDimensionPixelSize(R.styleable.SignTextView_padding, 0)
                text_view.setPadding(padding, padding, padding, padding)
            } finally {
                recycle()
            }
        }
    }
}
