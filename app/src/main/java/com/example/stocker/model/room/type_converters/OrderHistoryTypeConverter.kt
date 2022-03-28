package com.example.stocker.model.room.type_converters

import androidx.room.TypeConverter
import java.util.stream.Collectors

class OrderHistoryTypeConverter {



    @TypeConverter
    fun fromStringArrayToString(data:Array<String>):String{
       return arrayToString(data)
    }

    @TypeConverter
    fun fromIntArrayToString(data:Array<Int>):String{
        return arrayToString(data)
    }

    @TypeConverter
    fun fromLongArrayToString(data:Array<Long>):String{
        return arrayToString(data)
    }

    @TypeConverter
    fun fromStringToStringArray(str:String):Array<String>{
        return strToStringList(str).toTypedArray()
    }
    @TypeConverter
    fun fromStringToLongArray(str:String):Array<Long>{
        return strToStringList(str).stream().map { data->data.toLong() }.collect(Collectors.toList()).toTypedArray()
    }
    @TypeConverter
    fun fromStringToIntArray(str:String):Array<Int>{
        return strToStringList(str).stream().map { data->data.toInt() }.collect(Collectors.toList()).toTypedArray()
    }




    private fun strToStringList(str:String)=str.split("-")

    private fun arrayToString(data:Array<*>):String{
        return StringBuilder().apply {
            for(i in data.indices){
                append(data[i])
                if(i<data.size-1) append("-")
            }
        }.toString()
    }
}