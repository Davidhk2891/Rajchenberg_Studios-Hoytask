package com.rajchenbergstudios.hoytask.utils

import android.view.View
import android.view.animation.Animation

class HTSKAnimationUtils {

    companion object {
        fun setViewAnimation(
            v1: View,
            v2: View? = null,
            a: Animation
        ) {
            runAnimations(v1, v2, a)
        }

        private fun runAnimations(
            v1: View,
            v2: View? = null,
            a: Animation
        ) {
            v1.startAnimation(a)
            when {
                v2 != null -> {
                    v2.startAnimation(a)
                }
            }
        }
    }
}