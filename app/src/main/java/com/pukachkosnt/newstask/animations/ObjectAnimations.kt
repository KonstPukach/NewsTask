package com.pukachkosnt.newstask.animations

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.animation.DecelerateInterpolator
import android.widget.TextView

fun showMoreTextViewAnimation(textView: TextView, textViewShowMore: TextView) {
    val animationYdown = ObjectAnimator.ofFloat(textView, "translationY", -140f)
    val animationYup2 = ObjectAnimator.ofFloat(textViewShowMore, "translationY", -140f)
    val animationYup = ObjectAnimator.ofFloat(textView, "translationY", -150f)
    val set = AnimatorSet()
    set.play(animationYdown)
        .after(animationYup)
        .with(animationYup2)
    set.duration = 180
    set.interpolator = DecelerateInterpolator()
    set.start()
}

fun hideMoreTextViewAnimation(textView: TextView, textViewShowMore: TextView) {
    val animationYDown = ObjectAnimator.ofFloat(textView, "translationY", 0f)
    val animationY2Down = ObjectAnimator.ofFloat(textViewShowMore, "translationY", 0f)
    val animationYUp = ObjectAnimator.ofFloat(textView, "translationY", -150f)
    val set = AnimatorSet()
    set.play(animationYDown)
        .after(animationYUp)
        .with(animationY2Down)
    set.duration = 180
    set.interpolator = DecelerateInterpolator()
    set.start()
}