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


        outRect.top = space/2
        outRect.right=space
        outRect.left=space

        if(parent.getChildAdapterPosition(view)>=(state.itemCount-2)){
            outRect.bottom = space
        }
        else{
            outRect.bottom=space/2
        }


    }
}