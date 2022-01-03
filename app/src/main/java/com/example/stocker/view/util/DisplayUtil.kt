package com.example.stocker.view.util

import android.app.Activity
import android.graphics.Insets
import android.graphics.Point
import android.view.WindowInsets


class DisplayUtil {

    companion object {

        private fun density(activity: Activity) = activity.resources.displayMetrics.density

        fun getDisplaySize(activity: Activity): Point {

            val displayMetrics=activity.resources.displayMetrics





            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                val windowMetrics = activity.windowManager.currentWindowMetrics
                val insets: Insets =
                    windowMetrics.windowInsets.getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())


                return Point().apply {
                    x =
                        ((windowMetrics.bounds.height() - insets.top - insets.bottom )) // bounds gives size of match_parent . insets gives views surrounding it.
                    y = ((windowMetrics.bounds.width() - insets.left - insets.right))
                }
            } else {

                return Point().apply {
                    density(activity)
                    x = (displayMetrics.heightPixels)
                    y = (displayMetrics.widthPixels)
                }
            }


        }

        fun getOrientation(activity: Activity) = activity.resources.configuration.orientation

        fun dpToPixel(activity:Activity, dp:Int)=(dp* density(activity)).toInt()

    }
}