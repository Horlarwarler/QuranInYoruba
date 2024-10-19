package com.sadaqaworks.yorubaquran.util

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import javax.inject.Inject

class CustomVerseNumberTextView @Inject constructor(
    context: Context,
    attributeSets:AttributeSet? = null,
    styleAttribute:Int = 0
) : AppCompatTextView(context, attributeSets,styleAttribute) {

    init {
        text = "Text"
        this.text = "dd"

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = measuredWidth
        val height = measuredHeight
        val size = maxOf(width,height)
      //  setMeasuredDimension(size,size)
    }
//
//    override fun setBackground(background: Drawable?) {
//        super.setBackground(background)
//        val horizontalPadding = (width - height) / 2
//        val verticalPadding = (height - lineHeight) / 2
//        setPadding(horizontalPadding, verticalPadding, horizontalPadding, verticalPadding)
//
//    }

}