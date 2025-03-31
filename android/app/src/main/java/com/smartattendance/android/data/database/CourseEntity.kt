package com.smartattendance.android.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "courses")
data class CourseEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val lecturerId: String,
    val lecturerName: String,
    val createdAt: Date
)