package com.example.stocker.view.adapter

interface SelectionListener {
    fun onOneSelect()
    fun onMultipleSelect()
    fun selectionDisabled()
}