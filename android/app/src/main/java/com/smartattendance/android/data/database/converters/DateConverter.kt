package com.smartattendance.android.data.database.converters

import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.util.Date

class DateConverter {
    @TypeConverter  //  <--- Annotation on the function
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter  //  <--- Annotation on the function
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}