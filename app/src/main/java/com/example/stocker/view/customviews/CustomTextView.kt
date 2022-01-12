package com.example.stocker.view.customviews

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.inputmethod.EditorInfo
import com.google.android.material.textfield.TextInputEditText
import kotlin.math.absoluteValue

const val pixelThreshold=10
const val timeThreshold=600
const val press=0
const val longPress=1
const val scroll=2
class CustomTextView@JvmOverloads constructor(context: Context, attr: AttributeSet?=null):TextInputEditText(context,attr,0) {

    private var startX:Float = 0.0f
    private var startY:Float=0.0f
    private var startTime:Long=0L

    init {

        setBackgroundResource(android.R.color.transparent)
        isClickable=false
        isFocusable=false
        isCursorVisible=false
        isFocusableInTouchMode=false
        inputType=EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS
        maxLines=1
        //ellipsize=TextUtils.TruncateAt.END
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
         //super.onTouchEvent(event)

       // var action:Boolean=false
/*
        GestureDetector(context,object: GestureDetector.SimpleOnGestureListener() {

            override fun onDown(e: MotionEvent?): Boolean {
                action=true
                return super.onDown(e)
            }
            override fun onSingleTapUp(e: MotionEvent?): Boolean {

                action=false
                performClick()
                return super.onSingleTapUp(e)



            }

            override fun onScroll(
                e1: MotionEvent?,
                e2: MotionEvent?,
                distanceX: Float,
                distanceY: Float
            ): Boolean {

                action=true
                return super.onScroll(e1, e2, distanceX, distanceY)
            }

            override fun onLongPress(e: MotionEvent?) {
                action=false
                performLongClick()
                super.onLongPress(e)
            }
        })
*/
        
/*

        return   when(event?.action){
                MotionEvent.ACTION_DOWN->{
                    startX=event.x
                    startY=event.y
                    startTime=System.currentTimeMillis()
                    return true
                }
                MotionEvent.ACTION_UP->{
                    return when( isAClick(startX = startX, startY = startY, endX = event.x, endY = event.y, startTime = startTime, endTime = System.currentTimeMillis())){
                        press->{
                            isClickable=true
                            isFocusable=false
                            performClick()
                        false
                        }
                        longPress->{
                            isClickable=true
                            isFocusable=false
                            performLongClick()
                            false
                        }

                        scroll->{
                            isClickable=false
                            isFocusable=true
                           true
                        }
                        else -> {
                            true
                        }
                    }
                }


            else -> {
                false
            }
        }

 */
        return false// this views click event is passed to its parent(true if the view consumed the event otherwise false)
    }

    private fun isAClick(startX:Float,startY:Float,endX:Float,endY:Float,startTime:Long,endTime:Long):Int{
        val diffX= (startX-endX).absoluteValue
        val diffY = (startY-endY).absoluteValue
        val diffTime = (startTime-endTime).absoluteValue

        return if(!(diffX> pixelThreshold || diffY> pixelThreshold)) {
                if(diffTime> timeThreshold){
                    longPress
                }
                else{
                    press
                }
            }
            else{
                scroll
            }
        }

    }
