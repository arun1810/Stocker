package com.example.stocker.view.util

import android.app.Activity
import android.graphics.Insets
import android.graphics.Point
import android.util.DisplayMetrics
import android.view.WindowInsets
import com.example.stocker.R


class DisplayUtil {

    companion object {

        fun displaySizeInDp(activity: Activity):Display{
            val size = getDisplaySize(activity)
            return Display(height = pixelToDp(activity,size.height), width = pixelToDp(activity,size.width))
        }

        fun getNumberOfColumn(screenWidth:Int):Int{
            return when(screenWidth){
                in 0..599 ->{
                   4
                }
                in 600..904->{
                    8
                }
                in 905..1239->{
                    12
                }
                in 1240..1439->{
                    12
                }
                else->{
                    12
                }

            }
        }

        fun getBodyParams(activity: Activity):BodyParams{
            val displaySize =  getDisplaySize(activity)
            val numberOfColumn= getNumberOfColumn(pixelToDp(activity,displaySize.width))
            val totalMargin = activity.resources.getDimensionPixelSize(R.dimen.margin) * 2
            //activity.resources.getDimensionPixelSize()
            val totalGutter = activity.resources.getDimensionPixelSize(R.dimen.gutter) * (numberOfColumn-1)
            val bodySize =     pixelToDp(activity,(displaySize.width))

            return BodyParams(widthInDp = bodySize, numberOfColumns = numberOfColumn, columnSize = (bodySize/numberOfColumn), margin = R.dimen.margin, gutter = R.dimen.gutter)

        }


        private fun density(activity: Activity) = activity.resources.displayMetrics.density

        fun getDisplaySize(activity: Activity): Display {

            val displayMetrics=activity.resources.displayMetrics





            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                val windowMetrics = activity.windowManager.currentWindowMetrics
                val insets: Insets =
                    windowMetrics.windowInsets.getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())


                return Display(
                    height = (windowMetrics.bounds.height() - insets.top - insets.bottom), // bounds gives size of match_parent . insets gives views surrounding it.
                    width =  (windowMetrics.bounds.width() - insets.left - insets.right)
                )
            } else {

                return Display(height =displayMetrics.heightPixels, width = displayMetrics.widthPixels )

            }


        }

        fun getOrientation(activity: Activity) = activity.resources.configuration.orientation

        fun dpToPixel(activity:Activity, dp:Int):Int{

            println("pixel ${(dp* density(activity)).toInt()}")
            return (dp*density(activity)).toInt()
        }
        fun pixelToDp(activity:Activity, pixel:Int):Int{
            println("dp ${(pixel/density(activity)).toInt()}")
            return (pixel/density(activity)).toInt()
        }

    }
}