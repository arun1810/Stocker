package com.example.stocker.view.util

import android.app.Activity
import android.content.Context
import android.graphics.Insets
import android.graphics.Point
import android.util.DisplayMetrics
import android.view.Display
import android.view.WindowInsets
import android.view.WindowManager




class DisplayUtil {

    companion object {

        fun getDisplaySize(activity: Activity): Point {

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                val windowMetrics = activity.windowManager.currentWindowMetrics
                val insets: Insets =
                    windowMetrics.windowInsets.getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())


                return Point().apply {
                    x =
                        windowMetrics.bounds.height() - insets.top - insets.bottom // bounds gives size of match_parent . insets gives views surrounding it.
                    y = windowMetrics.bounds.width() - insets.left - insets.right
                }
            } else {
                val displayMetrics = DisplayMetrics()

                activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
                return Point().apply {
                    x = displayMetrics.heightPixels
                    y = displayMetrics.widthPixels
                }
            }


        }

        fun getOrientation(activity: Activity) = activity.resources.configuration.orientation


    }
}