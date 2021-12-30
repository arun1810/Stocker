package com.example.stocker.view.customviews

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.inputmethod.EditorInfo
import com.google.android.material.textfield.TextInputEditText

class ScrollableTextView@JvmOverloads constructor(context: Context, attr: AttributeSet?=null):TextInputEditText(context,attr,0) {

    init {
        setBackgroundResource(android.R.color.transparent)
        isClickable=false
        isFocusable=false
        isCursorVisible=false
        isFocusableInTouchMode=false
        inputType=EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS
        maxLines=1
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
         super.onTouchEvent(event)
        return false// this views click event is passed to its parent(true if the view consumed the event otherwise false)
    }
}