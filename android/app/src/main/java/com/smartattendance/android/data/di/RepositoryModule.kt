package com.smartattendnance.core.data.di

import com.smartattendnance.core.data.repository.AttendanceRepository
import com.smartattendnance.core.data.repository.AuthRepository
import com.smartattendnance.core.data.repository.CourseRepository
import com.smartattendnance.core.data.repository.UserPreferencesRepository
import com.smartattendnance.core.domain.repository.IAttendanceRepository
import com.smartattendnance.core.domain.repository.IAuthRepository
import com.smartattendnance.core.domain.repository.ICourseRepository
import com.smartattendnance.core.domain.repository.IUserPreferencesRepository


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