package com.example.stocker.viewmodel.helper

const val cantRetrieveData=-2
const val deleteError=-3
const val default=-4
const val uniqueIdError=-5
const val otherError=-6
const val validationError=-7


class Error {
    var msg= default
    var isHandled = true



}