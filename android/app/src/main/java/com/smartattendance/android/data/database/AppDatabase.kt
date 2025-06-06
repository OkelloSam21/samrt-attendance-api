package com.smartattendance.android.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.smartattendance.android.data.database.converters.DateConverter
import com.smartattendance.android.data.database.dao.AttendanceDao
import com.smartattendance.android.data.database.dao.CourseDao
import com.smartattendance.android.data.database.dao.UserDao


@Database(
    entities = [
        UserEntity::class,
        CourseEntity::class,
        AttendanceEntity::class,
        AttendanceSessionEntity::class
    ],
    views = [
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun courseDao(): CourseDao
    abstract fun attendanceDao(): AttendanceDao

    companion object {
        private const val DATABASE_NAME = "smart-attendance-db"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                ).fallbackToDestructiveMigration(false)
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}