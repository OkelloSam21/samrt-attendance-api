package di

import dagger.Component
import db.DatabaseFactory
import features.assignments.services.AssignmentService
import features.attendance.services.AttendanceService
import features.auth.services.AuthService
import features.auth.services.JwtService
import features.auth.util.RoleAuthorization
import features.courses.services.CourseService
import features.users.services.UserService
import javax.inject.Singleton

/**
 * Main application component for Hilt dependency injection
 */
@Singleton
@Component(modules = [
    AppModule::class,
    AuthModule::class,
    UserModule::class,
    CourseModule::class,
    AttendanceModule::class,
    AssignmentModule::class
])
interface ApplicationComponent {
    // Core services
    fun databaseFactory(): DatabaseFactory
    
    // Auth and security
    fun jwtService(): JwtService
    fun authService(): AuthService
    fun roleAuthorization(): RoleAuthorization
    
    // Feature services
    fun userService(): UserService
    fun courseService(): CourseService
    fun attendanceService(): AttendanceService
    fun assignmentService(): AssignmentService
    
    @Component.Builder
    interface Builder {
        fun build(): ApplicationComponent
    }
}