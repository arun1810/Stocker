package com.example.stocker.view.customviews

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import com.example.stocker.repository.helper.SortUtil

private const val sortImgKey="sortOrder"
private const val parcelKey="parcel"

class SortImageButton@JvmOverloads constructor(context: Context, attr: AttributeSet?=null): androidx.appcompat.widget.AppCompatImageButton(context,attr,0) {

    private var _sortOrder=SortUtil.SortOrder.DEC
    private  var currentIcon:Int=0

    val sortOrder
    get() = _sortOrder

    var ascIcon=0
    var decIcon=0

    var neutralIcon=0
    set(value){
        currentIcon=value
        field = value
    }

    fun changeSortOrder(){
        _sortOrder = when(_sortOrder) {
            SortUtil.SortOrder.ASC->{
                setImageResource(decIcon)
                currentIcon=decIcon
                SortUtil.SortOrder.DEC
            }
            SortUtil.SortOrder.DEC->{
                setImageResource(ascIcon)
                currentIcon=ascIcon
                SortUtil.SortOrder.ASC
            }

        }
    }

    fun changeToDefaultSortOrder(){
        setImageResource(neutralIcon)
        currentIcon=neutralIcon
        _sortOrder= SortUtil.SortOrder.DEC
    }

    override fun onSaveInstanceState(): Parcelable {

        val bundle = Bundle()
        bundle.putParcelable(parcelKey,super.onSaveInstanceState())
        bundle.putInt(sortImgKey,currentIcon)
        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable?) {

        (state as Bundle).let {
            super.onRestoreInstanceState(it.getParcelable(parcelKey))
            currentIcon=it.getInt(sortImgKey)
            setImageResource(currentIcon)
            _sortOrder = if(currentIcon==ascIcon){
                SortUtil.SortOrder.ASC
            } else{
                SortUtil.SortOrder.DEC
            }
        }
    }
}