package com.akas62083.task_app.db

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.toColor
import androidx.room.TypeConverter
import java.time.LocalDate

class Converters {
    // LocalDate用
    @TypeConverter
    fun fromTimestamp(value: Long?): LocalDate? {
        return value?.let { LocalDate.ofEpochDay(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDate?): Long? {
        return date?.toEpochDay()
    }

    // Color用
   // @TypeConverter
   // fun fromColor(color: Color): Int {
   //     return color.toArgb()
   // }

   // @TypeConverter
   // fun toColor(colorInt: Int): Color {
   //     return Color(colorInt)
   // }
}