package com.rajchenbergstudios.hoytask.utils

import android.view.View

class HoytaskViewStateUtils {

    companion object {
        fun setViewVisibility(v1: View, v2: View? = null, v3: View? = null, v4: View? = null, visibility: Int){
            setVisibility(v1, v2, v3, v4, visibility)
        }

        fun setViewClickState(v1: View, v2: View? = null, v3: View? = null, v4: View? = null, clickable: Boolean){
            setClickable(v1, v2, v3, v4, clickable)
        }

        private fun setVisibility(v1: View, v2: View?, v3: View?, v4: View?, visibility: Int){
            v1.visibility = visibility
            v2?.visibility = visibility
            v3?.visibility = visibility
            v4?.visibility = visibility
        }

        private fun setClickable(v1: View, v2: View?, v3: View?, v4: View?, clickable: Boolean){
            v1.isClickable = clickable
            v2?.isClickable = clickable
            v3?.isClickable = clickable
            v4?.isClickable = clickable
        }
    }
}