package com.smartattendance.android.data.di

import com.smartattendance.android.data.repository.AttendanceRepository
import com.smartattendance.android.data.repository.AuthRepository
import com.smartattendance.android.data.repository.CourseRepository
import com.smartattendance.android.data.repository.UserPreferencesRepository
import com.smartattendance.android.domain.repository.IAttendanceRepository
import com.smartattendance.android.domain.repository.IAuthRepository
import com.smartattendance.android.domain.repository.ICourseRepository
import com.smartattendance.android.domain.repository.IUserPreferencesRepository


import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepository: AuthRepository
    ): IAuthRepository

    @Binds
    @Singleton
    abstract fun bindUserPreferencesRepository(
        userPreferencesRepository: UserPreferencesRepository
    ): IUserPreferencesRepository

    @Binds
    @Singleton
    abstract fun bindCourseRepository(
        courseRepository: CourseRepository
    ): ICourseRepository

    @Binds
    @Singleton
    abstract fun bindAttendanceRepository(
        attendanceRepository: AttendanceRepository
    ): IAttendanceRepository
}