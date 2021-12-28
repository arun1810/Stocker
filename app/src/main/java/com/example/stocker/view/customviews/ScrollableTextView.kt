package com.example.stocker.view.customviews

import android.content.Context
import android.util.AttributeSet
import android.view.inputmethod.EditorInfo
import com.google.android.material.textfield.TextInputEditText

class ScrollableTextView@JvmOverloads constructor(context: Context, attr: AttributeSet?=null):TextInputEditText(context,attr,0) {

    init {
        setBackgroundResource(android.R.color.transparent)
        isClickable=false
        isFocusable=false
        isCursorVisible=false
        inputType=EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS
        maxLines=1
    }
}