package com.pukachkosnt.newstask.animations

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.animation.DecelerateInterpolator
import android.widget.LinearLayout

const val SHOW_MORE_ANIM_DURATION = 180L
const val START_Y_POSITION = 0f
const val INTERMEDIATE_Y_POSITION = -190f
const val SINGLE_LINE_HEIGHT = 25f


fun showMoreTextViewAnimation(linLayout: LinearLayout, linesCount: Int) {
    val animationYDown = ObjectAnimator.ofFloat(linLayout, "translationY",
        -SINGLE_LINE_HEIGHT * (linesCount + 1))
    val animationYup = ObjectAnimator.ofFloat(linLayout, "translationY",
        -SINGLE_LINE_HEIGHT * (linesCount + 2))
    val set = AnimatorSet()
    set.play(animationYDown)
        .with(animationYup)
    set.duration = SHOW_MORE_ANIM_DURATION
    set.interpolator = DecelerateInterpolator()
    set.start()
}

fun hideMoreTextViewAnimation(linLayout: LinearLayout) {
    val animationYDown = ObjectAnimator.ofFloat(linLayout, "translationY", START_Y_POSITION)
    val animationYUp = ObjectAnimator.ofFloat(linLayout, "translationY", INTERMEDIATE_Y_POSITION)
    val set = AnimatorSet()
    set
        .play(animationYUp)
        .with(animationYDown)
    set.duration = SHOW_MORE_ANIM_DURATION
    set.interpolator = DecelerateInterpolator()
    set.start()
}