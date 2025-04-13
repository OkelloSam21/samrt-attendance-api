package di

import config.AppConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import features.assignments.repositories.AssignmentRepository
import features.assignments.repositories.AssignmentRepositoryImpl
import features.assignments.services.AssignmentService
import features.assignments.services.AssignmentServiceImpl
import features.attendance.repositories.AttendanceRepository
import features.attendance.repositories.AttendanceRepositoryImpl
import features.attendance.services.AttendanceService
import features.attendance.services.AttendanceServiceImpl
import features.auth.repositories.AuthRepository
import features.auth.repositories.AuthRepositoryImpl
import features.auth.services.AuthService
import features.auth.services.AuthServiceImpl
import features.auth.services.JwtService
import features.auth.util.RoleAuthorization
import features.auth.util.RoleAuthorizationImpl
import features.courses.repositories.CourseRepository
import features.courses.repositories.CourseRepositoryImpl
import features.courses.services.CourseService
import features.courses.services.CourseServiceImpl
import features.users.repositories.StaffRepository
import features.users.repositories.StaffRepositoryImpl
import features.users.repositories.StudentRepository
import features.users.repositories.StudentRepositoryImpl
import features.users.repositories.UserRepository
import features.users.repositories.UserRepositoryImpl
import features.users.services.UserService
import features.users.services.UserServiceImpl
import javax.inject.Singleton

/**
 * Core application module
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun provideDatabaseFactory(): DatabaseFactory {
        return DatabaseFactory()
    }
    
    @Provides
    @Singleton
    fun provideJwtService(): JwtService {
        return JwtService()
    }
    
    @Provides
    @Singleton
    fun provideRoleAuthorization(): RoleAuthorization {
        return RoleAuthorizationImpl()
    }
}