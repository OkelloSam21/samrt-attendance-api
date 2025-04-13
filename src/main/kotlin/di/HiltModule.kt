package di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import features.assignments.repositories.AssignmentRepository
import features.assignments.repositories.AssignmentRepositoryImpl
import features.assignments.repositories.GradeRepository
import features.assignments.repositories.SubmissionRepository
import features.assignments.services.AssignmentService
import features.assignments.services.AssignmentServiceImpl
import features.attendance.repositories.AttendanceRepository
import features.attendance.repositories.AttendanceRepositoryImpl
import features.attendance.repositories.AttendanceSessionRepository
import features.attendance.services.AttendanceService
import features.attendance.services.AttendanceServiceImpl
import features.auth.repositories.AuthRepository
import features.auth.repositories.AuthRepositoryImpl
import features.auth.services.AuthService
import features.auth.services.AuthServiceImpl
import features.auth.services.JwtService
import features.courses.repositories.CourseRepository
import features.courses.repositories.CourseRepositoryImpl
import features.courses.repositories.CourseScheduleRepository
import features.courses.services.CourseService
import features.courses.services.CourseServiceImpl
import features.users.repositories.*
import features.users.services.UserService
import features.users.services.UserServiceImpl
import javax.inject.Singleton


/**
 * Authentication module
 */
@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    @Singleton
    fun provideAuthRepository(): AuthRepository {
        return AuthRepositoryImpl()
    }

    @Provides
    @Singleton
    fun provideAuthService(
        authRepository: AuthRepository,
        userRepository: UserRepository,
        studentRepository: StudentRepository,
        staffRepository: StaffRepository,
        jwtService: JwtService
    ): AuthService {
        return AuthServiceImpl(
            authRepository,
            userRepository,
            studentRepository,
            staffRepository,
            jwtService
        )
    }
}

/**
 * User management module
 */
@Module
@InstallIn(SingletonComponent::class)
object UserModule {

    @Provides
    @Singleton
    fun provideUserRepository(): UserRepository {
        return UserRepositoryImpl()
    }

    @Provides
    @Singleton
    fun provideStudentRepository(): StudentRepository {
        return StudentRepositoryImpl()
    }

    @Provides
    @Singleton
    fun provideStaffRepository(): StaffRepository {
        return StaffRepositoryImpl()
    }

    @Provides
    @Singleton
    fun provideUserService(
        userRepository: UserRepository,
        studentRepository: StudentRepository,
        staffRepository: StaffRepository
    ): UserService {
        return UserServiceImpl(userRepository, studentRepository, staffRepository)
    }
}

/**
 * Course management module
 */
@Module
@InstallIn(SingletonComponent::class)
object CourseModule {

    @Provides
    @Singleton
    fun provideCourseRepository(): CourseRepository {
        return CourseRepositoryImpl()
    }

    @Provides
    @Singleton
    fun provideCourseService(
        courseRepository: CourseRepository,
        courseScheduleRepository: CourseScheduleRepository,
        userRepository: UserRepository
    ): CourseService {
        return CourseServiceImpl(courseRepository, courseScheduleRepository,userRepository)
    }
}

/**
 * Attendance management module
 */
@Module
@InstallIn(SingletonComponent::class)
object AttendanceModule {

    @Provides
    @Singleton
    fun provideAttendanceRepository(): AttendanceRepository {
        return AttendanceRepositoryImpl()
    }

    @Provides
    @Singleton
    fun provideAttendanceService(
        attendanceSessionRepository: AttendanceSessionRepository,
        attendanceRepository: AttendanceRepository,
        courseRepository: CourseRepository,
        userRepository: UserRepository
    ): AttendanceService {
        return AttendanceServiceImpl(attendanceSessionRepository, attendanceRepository, courseRepository, userRepository)
    }
}

/**
 * Assignment management module
 */
@Module
@InstallIn(SingletonComponent::class)
object AssignmentModule {

    @Provides
    @Singleton
    fun provideAssignmentRepository(): AssignmentRepository {
        return AssignmentRepositoryImpl()
    }

    @Provides
    @Singleton
    fun provideAssignmentService(
        assignmentRepository: AssignmentRepository,
        courseRepository: CourseRepository,
        userRepository: UserRepository,
        submissionRepository: SubmissionRepository,
        gradeRepository: GradeRepository
    ): AssignmentService {
        return AssignmentServiceImpl(assignmentRepository, submissionRepository,gradeRepository,courseRepository, userRepository)
    }
}