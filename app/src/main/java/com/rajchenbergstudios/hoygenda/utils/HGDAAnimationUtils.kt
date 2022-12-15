package com.rajchenbergstudios.hoygenda.utils

import android.app.Activity
import android.app.Dialog
import android.view.View
import android.view.animation.Animation
import com.facebook.shimmer.ShimmerFrameLayout

// private const val TAG = "HGDAAnimationUtils.kt"

class HGDAAnimationUtils {

    companion object {
        fun setViewAnimation(
            v1: View? = null,
            v2: View? = null,
            a: Animation
        ) {
            runAnimations(v1, v2, a)
        }

        fun setViewAnimation(
            v1: View? = null,
            v2: View? = null,
            v3: View? = null,
            a: Animation
        ) {
            runAnimations(v1, v2, v3, a)
        }

        private fun runAnimations(
            v1: View? = null,
            v2: View? = null,
            a: Animation
        ) {
            v1?.startAnimation(a)
            v2?.startAnimation(a)
        }

        private fun runAnimations(
            v1: View? = null,
            v2: View? = null,
            v3: View? = null,
            a: Animation
        ) {
            v1?.startAnimation(a)
            v2?.startAnimation(a)
            v3?.startAnimation(a)
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