package com.example.stocker.model.room.type_converters

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class DateTypeConverter {

    @TypeConverter
    fun fromDateToString(date: LocalDate):String{
        return date.toString()
    }

    @TypeConverter
    fun fromStringToDate(date:String): LocalDate {
        return LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    }


}