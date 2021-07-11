package com.pukachkosnt.newstask.animations

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.DecelerateInterpolator
import android.view.animation.ScaleAnimation

const val ANIM_TRANSLATE_DURATION = 180L

fun moveViewPosition(view: View, positionFinish: Float, positionIntermediate: Float) {
    val animationYDown = ObjectAnimator.ofFloat(view, "translationY", positionFinish)
    val animationYUp = ObjectAnimator.ofFloat(view, "translationY", positionIntermediate)
    val set = AnimatorSet()
    set.play(animationYUp)
        .before(animationYDown)
    set.duration = ANIM_TRANSLATE_DURATION
    set.interpolator = DecelerateInterpolator()
    set.start()
}

const val ANIM_SCALE_DURATION = 180L

fun scaleViewFromZero(view: View) {
    val scaleAnimToBig = ScaleAnimation(0.1f, 1.2f, 0.1f, 1.2f,
        Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
    val scaleAnimToSmall = ScaleAnimation(1.2f, 1f, 1.2f, 1f,
        Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
    val set = AnimationSet(false)
    set.addAnimation(scaleAnimToBig)
    set.addAnimation(scaleAnimToSmall)
    set.duration = ANIM_SCALE_DURATION
    set.interpolator = DecelerateInterpolator()
    view.startAnimation(set)
}