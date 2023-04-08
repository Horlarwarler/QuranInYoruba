package com.sadaqaworks.yorubaquran.util

import android.animation.ValueAnimator
import android.view.View
import android.view.ViewTreeObserver

class ItemAnimator(view: View) {

    val startHeight = view.height
    val valueAnimator = ValueAnimator.ofInt(startHeight, 0)
    init {
        valueAnimator.duration = 500
        view.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                // Measure the view's height after it has been laid out
                val endHeight = view.measuredHeight

                // Animate the view's height to the measured height
                val valueAnimator = ValueAnimator.ofInt(startHeight, endHeight)
                valueAnimator.duration = 500
                valueAnimator.addUpdateListener { animation ->
                    val animatedValue = animation.animatedValue as Int
                    val layoutParams = view.layoutParams
                    layoutParams.height = animatedValue
                    view.layoutParams = layoutParams
                }
                valueAnimator.start()

                // Remove the OnGlobalLayoutListener to prevent it from being called multiple times
                view.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })

    }

    fun startAnimation(){
        valueAnimator.start()

    }

    fun stopAnimation(){
        valueAnimator.end()
    }


}