package com.example.stocker.view.listeners

import android.content.Context
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView

class RecyclerTouchListener(private val context: Context, private val recycler:RecyclerView, private val recyclerClickListener:RecyclerClickListener):RecyclerView.OnItemTouchListener {

   /* private var gestureDetector: GestureDetector = GestureDetector(context,object:GestureDetector.SimpleOnGestureListener(){
        override fun onSingleTapUp(e: MotionEvent?): Boolean {
            return false
        }

        override fun onLongPress(e: MotionEvent?) {
            val view = e?.let{ recycler.findChildViewUnder(e.x, e.y) }

            if(view!=null)
                recyclerClickListener.onLongClick(view,recycler.getChildLayoutPosition(view))
        }

    })
    */



    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        val view = recycler.findChildViewUnder(e.x,e.y)
        if(view!=null)
            recyclerClickListener.onClick(view,recycler.getChildLayoutPosition(view))

        return true
    }

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {


    }

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {

    }

}