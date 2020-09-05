package com.duke.xial.elliot.kim.kotlin.egglotto.database

import androidx.room.TypeConverter
import com.google.gson.Gson

class Converters {
    @TypeConverter
    fun arrayToJson(value: Array<Int>) = Gson().toJson(value)

    @TypeConverter
    fun jsonToArray(value: String) = Gson().fromJson(value, Array<Int>::class.java)
}