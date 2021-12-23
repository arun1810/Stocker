package com.example.stocker.view.adapter.decorator

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.stocker.R

class FabRecyclerDecorator(val space:Int):RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.top=space
        outRect.left = 5
        outRect.right = 5
        if(parent.getChildLayoutPosition(view)==state.itemCount-1){
            outRect.bottom= (space*2.5).toInt()
        }
    }
}