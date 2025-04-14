package di

import dagger.Module
import dagger.Provides
import features.assignments.repositories.*
import features.assignments.services.AssignmentService
import features.assignments.services.AssignmentServiceImpl
import features.attendance.repositories.AttendanceRepository
import features.attendance.repositories.AttendanceRepositoryImpl
import features.attendance.repositories.AttendanceSessionRepository
import features.attendance.repositories.AttendanceSessionRepositoryImpl
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
import features.courses.repositories.CourseScheduleRepositoryImpl
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
object CourseModule {

    @Provides
    @Singleton
    fun provideCourseRepository(): CourseRepository {
        return CourseRepositoryImpl()
    }

    @Provides
    @Singleton
    fun provideCourseScheduleRepository(): CourseScheduleRepository {
        return CourseScheduleRepositoryImpl()
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
object AttendanceModule {

    @Provides
    @Singleton
    fun provideAttendanceRepository(): AttendanceRepository {
        return AttendanceRepositoryImpl()
    }

    @Provides
    @Singleton
    fun provideAttendanceSessionRepository(): AttendanceSessionRepository {
        return AttendanceSessionRepositoryImpl()
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
object AssignmentModule {

    @Provides
    @Singleton
    fun provideAssignmentRepository(): AssignmentRepository {
        return AssignmentRepositoryImpl()
    }

    @Provides
    @Singleton
    fun provideSubmissionRepository(): SubmissionRepository {
        return SubmissionRepositoryImpl()
    }

    @Provides
    @Singleton
    fun provideGradeRepository(): GradeRepository {
        return GradeRepositoryImpl()
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