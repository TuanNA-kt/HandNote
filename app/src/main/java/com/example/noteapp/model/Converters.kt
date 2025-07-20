package com.example.noteapp.model

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromList(value: List<String>?): String? {
        return value?.joinToString(separator = "||")
    }

    @TypeConverter
    fun toList(value: String?): List<String>? {
        return value?.split("||")
    }
}
