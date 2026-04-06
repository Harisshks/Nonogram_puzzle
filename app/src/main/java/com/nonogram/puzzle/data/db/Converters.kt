package com.nonogram.puzzle.data.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    @TypeConverter
    fun fromString(value: String): List<Int> = Gson().fromJson(value, object : TypeToken<List<Int>>() {}.type)

    @TypeConverter
    fun fromList(list: List<Int>): String = Gson().toJson(list)
}
