package com.smartattendance.android.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.smartattendance.android.data.database.converters.DateConverter
import java.util.Date

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val email: String,
    val role: String,
    val regNo: String? = null,
    val employeeRoleNo: String? = null,
    @TypeConverters(DateConverter::class)
    val createdAt: Date,
    @TypeConverters(DateConverter::class)
    val updatedAt: Date
)