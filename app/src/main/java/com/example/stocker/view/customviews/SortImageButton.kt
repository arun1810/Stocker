package com.example.stocker.view.customviews

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.example.stocker.repository.helper.SortUtil

class SortImageButton@JvmOverloads constructor(context: Context, attr: AttributeSet?=null): AppCompatImageView(context,attr,0) {
    private var _sortOrder:SortUtil.SortOrder = SortUtil.SortOrder.ASC

    val sortOrder
    get() = _sortOrder

    var ascIcon=0
    var dscIcon=0
    init {
        isClickable=true
    }
    fun changeSortOrder(){
        _sortOrder = if(_sortOrder== SortUtil.SortOrder.ASC){
                            setImageResource(dscIcon)
                            SortUtil.SortOrder.DEC
                    }
                    else{
                            setImageResource(ascIcon)
                            SortUtil.SortOrder.ASC
                    }
    }

    fun changeToDefaultSortOrder(){
        setImageResource(ascIcon)
        _sortOrder= SortUtil.SortOrder.ASC
    }


}