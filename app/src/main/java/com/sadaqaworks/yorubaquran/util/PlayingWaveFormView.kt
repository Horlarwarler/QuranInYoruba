package com.sadaqaworks.yorubaquran.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class PlayingWaveFormView(context: Context, attributeSet: AttributeSet): View(context,attributeSet) {

    private var paint = Paint()

    init {
        paint.color = Color.rgb(244,81,30)
    }
    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
       // canvas?.drawRoundRect(RectF(20f,30f,50f,60f),10f,10f,paint)
        canvas?.drawRoundRect(RectF(0f,0f,50f,60f),10f,10f,paint)
    }

}