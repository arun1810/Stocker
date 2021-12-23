package com.example.stocker.view.adapter.decorator

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class StockDecorator(private val space:Int):RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.top = space
        outRect.left = space
        outRect.right = space
        if(parent.getChildLayoutPosition(view)==state.itemCount-1){
            outRect.bottom=space*12
        }

    }
}