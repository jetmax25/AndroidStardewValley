package com.pickledgames.stardewvalleyguide.views

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.RelativeLayout
import com.pickledgames.stardewvalleyguide.R
import com.pickledgames.stardewvalleyguide.databinding.SignTextViewBinding

class SignTextView : RelativeLayout {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setup(attrs)
    }

    private val binding: SignTextViewBinding =
        SignTextViewBinding.bind(LayoutInflater.from(context).inflate(R.layout.sign_text_view, this, false))
    private fun setup(attrs: AttributeSet?) {
        context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.SignTextView,
                0,
                0
        ).apply {
            try {
                val text = getText(R.styleable.SignTextView_text) ?: ""
                binding.textView.text = text

                val defaultTextSize = binding.textView.textSize ?: 14f
                val textSize = getDimension(R.styleable.SignTextView_textSize, defaultTextSize)
                binding.textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)

                val containerMarginLayoutParams = binding.signHeaderContainer.layoutParams as MarginLayoutParams
                val margin = getDimensionPixelSize(R.styleable.SignTextView_margin, 0)
                containerMarginLayoutParams.setMargins(margin, margin, margin, margin)

                val padding = getDimensionPixelSize(R.styleable.SignTextView_padding, 0)
                binding.textView.setPadding(padding, padding, padding, padding)
            } finally {
                recycle()
            }
        }
    }
}
