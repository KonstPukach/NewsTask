package com.pukachkosnt.newstask.animations

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.DecelerateInterpolator

const val ANIM_DURATION = 180L


fun moveViewPosition(view: View, positionFinish: Float, positionIntermediate: Float) {
    val animationYDown = ObjectAnimator.ofFloat(view, "translationY", positionFinish)
    val animationYUp = ObjectAnimator.ofFloat(view, "translationY", positionIntermediate)
    val set = AnimatorSet()
    set.play(animationYUp)
        .before(animationYDown)
    set.duration = ANIM_DURATION
    set.interpolator = DecelerateInterpolator()
    set.start()
}