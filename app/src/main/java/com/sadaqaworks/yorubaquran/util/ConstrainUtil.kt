package com.sadaqaworks.yorubaquran.util

import android.view.animation.AccelerateDecelerateInterpolator
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager

fun animateConstrainLayout(
    constrainLayout: ConstraintLayout,
    constraintSet: ConstraintSet,
    duration: Long
) {
    val trans = AutoTransition()
    trans.setDuration(duration)
    trans.setInterpolator(AccelerateDecelerateInterpolator())
    TransitionManager.beginDelayedTransition(
        constrainLayout,trans
    )
    constraintSet.applyTo(constrainLayout)
}
