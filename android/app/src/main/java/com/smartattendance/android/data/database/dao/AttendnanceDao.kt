package com.smartattendance.android.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.smartattendance.android.data.database.AttendanceEntity
import com.smartattendance.android.data.database.AttendanceSessionEntity
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface AttendanceDao {
    // Attendance Session methods
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendanceSession(session: AttendanceSessionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendanceSessions(sessions: List<AttendanceSessionEntity>)

    @Query("SELECT * FROM attendance_sessions WHERE id = :sessionId")
    fun getAttendanceSessionById(sessionId: String): Flow<AttendanceSessionEntity?>

    @Query("SELECT * FROM attendance_sessions WHERE lecturerId = :lecturerId ORDER BY createdAt DESC")
    fun getAttendanceSessionsByLecturerId(lecturerId: String): Flow<List<AttendanceSessionEntity>>

    @Query("SELECT * FROM attendance_sessions WHERE courseId = :courseId ORDER BY createdAt DESC")
    fun getAttendanceSessionsByCourseId(courseId: String): Flow<List<AttendanceSessionEntity>>

    @Query("SELECT * FROM attendance_sessions WHERE sessionCode = :sessionCode")
    fun getAttendanceSessionByCode(sessionCode: String): Flow<AttendanceSessionEntity?>

    @Query("SELECT * FROM attendance_sessions WHERE expiresAt > :currentTime ORDER BY createdAt DESC")
    fun getActiveAttendanceSessions(currentTime: Date): Flow<List<AttendanceSessionEntity>>

    @Query("DELETE FROM attendance_sessions WHERE id = :sessionId")
    suspend fun deleteAttendanceSession(sessionId: String)

    // Attendance records methods
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendanceRecord(attendance: AttendanceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendanceRecords(attendances: List<AttendanceEntity>)

    @Query("SELECT * FROM attendance WHERE id = :attendanceId")
    fun getAttendanceById(attendanceId: String): Flow<AttendanceEntity?>

    @Query("SELECT * FROM attendance WHERE sessionId = :sessionId")
    fun getAttendancesBySessionId(sessionId: String): Flow<List<AttendanceEntity>>

    @Query("SELECT * FROM attendance WHERE studentId = :studentId")
    fun getAttendancesByStudentId(studentId: String): Flow<List<AttendanceEntity>>

    @Query("SELECT * FROM attendance WHERE courseId = :courseId AND studentId = :studentId")
    fun getAttendancesByStudentAndCourse(studentId: String, courseId: String): Flow<List<AttendanceEntity>>

    @Query("SELECT COUNT(*) FROM attendance WHERE courseId = :courseId AND studentId = :studentId AND status = 'Present'")
    fun getAttendanceCountForStudentInCourse(studentId: String, courseId: String): Flow<Int>

    @Query("DELETE FROM attendance WHERE id = :attendanceId")
    suspend fun deleteAttendance(attendanceId: String)

    @Query("DELETE FROM attendance WHERE sessionId = :sessionId")
    suspend fun deleteAttendancesBySessionId(sessionId: String)

    @Transaction
    suspend fun deleteSessionWithAttendances(sessionId: String) {
        deleteAttendancesBySessionId(sessionId)
        deleteAttendanceSession(sessionId)
    }
}