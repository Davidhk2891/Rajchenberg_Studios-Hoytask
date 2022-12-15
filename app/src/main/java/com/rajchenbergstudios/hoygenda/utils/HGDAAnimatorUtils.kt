package com.rajchenbergstudios.hoygenda.utils

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Context
import android.util.Log
import android.view.View
import androidx.core.animation.doOnEnd
import com.rajchenbergstudios.hoygenda.R

private const val TAG = "HGDAAnimatorUtils.kt"

class HGDAAnimatorUtils {

    companion object {
        fun runFlipAnimation(context: Context, visibleView: View, inVisibleView: View) {
            try {
                visibleView.visibility = View.VISIBLE
                val flipOutAnimatorSet =
                    AnimatorInflater.loadAnimator(
                        context,
                        R.animator.flip_out
                    ) as AnimatorSet
                flipOutAnimatorSet.setTarget(inVisibleView)
                val flipInAnimatorSet =
                    AnimatorInflater.loadAnimator(
                        context,
                        R.animator.flip_in
                    ) as AnimatorSet
                flipInAnimatorSet.setTarget(visibleView)
                flipOutAnimatorSet.start()
                flipInAnimatorSet.start()
                flipInAnimatorSet.doOnEnd {
                    inVisibleView.visibility = View.GONE
                }
            } catch (e: Exception) {
                Log.i(TAG, "flipCard()", e.cause)
            }
        }
    }
}