package com.lacker.utils.extensions

import android.view.View
import android.view.animation.*
import androidx.annotation.AnimRes

fun View.animateFade() {

    val fadeOut = AlphaAnimation(1f, 0.5f)
    fadeOut.interpolator = AccelerateInterpolator()
    fadeOut.duration = 50

    val fadeIn = AlphaAnimation(0.5f, 1f)
    fadeIn.interpolator = DecelerateInterpolator()
    fadeIn.startOffset = 50
    fadeIn.duration = 50

    val animation = AnimationSet(false)
    animation.addAnimation(fadeIn)
    animation.addAnimation(fadeOut)
    startAnimation(animation)
}

fun View.animate(@AnimRes animId: Int) {
    startAnimation(AnimationUtils.loadAnimation(context, animId))
}

fun View.animateScale(onAnimationMiddle: (() -> Unit)? = null) {
    animate().apply {
        scaleX(0.97f)
        scaleY(0.97f)
        duration = 50
        withEndAction {
            onAnimationMiddle?.invoke()
            animate().apply {
                scaleX(1f)
                scaleY(1f)
                duration = 50
            }
        }
    }
}

fun View.appearFromRight(duration: Long = 500L) {
    animation?.setAnimationListener(null)
    animation?.cancel()
    clearAnimation()
    animate()
        .translationX(context.resources.displayMetrics.widthPixels.toFloat())
        .setDuration(0)
        .withEndAction {
            animate()
                .setInterpolator(DecelerateInterpolator())
                .translationX(0f)
                .withStartAction { visible() }
                .withEndAction { visible() }
                .duration = duration
        }
}

fun View.appearFromLeft(duration: Long = 500L) {
    animation?.setAnimationListener(null)
    animation?.cancel()
    clearAnimation()
    animate()
        .translationX(-context.resources.displayMetrics.widthPixels.toFloat())
        .setDuration(0)
        .withEndAction {
            animate()
                .setInterpolator(DecelerateInterpolator())
                .translationX(0f)
                .withStartAction { visible() }
                .withEndAction { visible() }
                .duration = duration
        }
}

fun View.hideOnLeft(duration: Long = 500L) {
    animate()
        .setInterpolator(DecelerateInterpolator())
        .withEndAction { gone() }
        .x(-context.resources.displayMetrics.widthPixels.toFloat())
        .duration = duration
}

fun View.hideOnRight(duration: Long = 500L) {
    animate()
        .setInterpolator(DecelerateInterpolator())
        .withEndAction { gone() }
        .x(context.resources.displayMetrics.widthPixels.toFloat())
        .duration = duration
}

fun View.appearFromBottom(duration: Long = 500L) {
    animate()
        .setInterpolator(DecelerateInterpolator())
        .withStartAction { visible() }
        .translationY(0f)
        .duration = duration
}

fun View.hideBelowBottom(duration: Long = 500L) {
    animate()
        .setInterpolator(AccelerateInterpolator())
        .withEndAction { gone() }
        .y(context.resources.displayMetrics.heightPixels.toFloat())
        .duration = duration
}