package com.rajchenbergstudios.hoygenda.utils

import android.app.Activity
import android.app.Dialog
import android.view.View
import android.view.animation.Animation
import com.facebook.shimmer.ShimmerFrameLayout

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

        fun startShimmerView(activity: Activity, shimmerLayout: Int) {
            val shimmerFrameLayout = activity.findViewById<ShimmerFrameLayout>(shimmerLayout)
            shimmerFrameLayout.startShimmer()
        }

        fun startShimmerView(dialog: Dialog, shimmerLayout: Int) {
            val shimmerFrameLayout = dialog.findViewById<ShimmerFrameLayout>(shimmerLayout)
            shimmerFrameLayout.startShimmer()
        }

        fun stopShimmerView(activity: Activity, shimmerLayout: Int) {
            val shimmerFrameLayout = activity.findViewById<ShimmerFrameLayout>(shimmerLayout)
            shimmerFrameLayout.stopShimmer()
            shimmerFrameLayout.visibility = View.GONE
        }

        fun stopShimmerView(dialog: Dialog, shimmerLayout: Int) {
            val shimmerFrameLayout = dialog.findViewById<ShimmerFrameLayout>(shimmerLayout)
            shimmerFrameLayout.stopShimmer()
            shimmerFrameLayout.visibility = View.GONE
        }
    }
}