package di

import db.DatabaseFactory
import features.assignments.services.AssignmentService
import features.assignments.services.AssignmentServiceImpl
import features.attendance.services.AttendanceService
import features.attendance.services.AttendanceServiceImpl
import features.auth.services.AuthService
import features.auth.services.AuthServiceImpl
import features.auth.services.JwtService
import features.auth.util.RoleAuthorization
import features.auth.util.RoleAuthorizationImpl
import features.courses.services.CourseService
import features.courses.services.CourseServiceImpl
import features.users.services.UserService
import features.users.services.UserServiceImpl
import javax.inject.Singleton

/**
 * Manual implementation of ApplicationComponent
 */
@Singleton
class ApplicationComponentImpl private constructor(
    private val databaseFactory: DatabaseFactory,
    private val jwtService: JwtService,
    private val roleAuthorization: RoleAuthorization,
    private val userService: UserService,
    private val authService: AuthService,
    private val courseService: CourseService,
    private val attendanceService: AttendanceService,
    private val assignmentService: AssignmentService
) : ApplicationComponent {

    override fun databaseFactory(): DatabaseFactory = databaseFactory
    override fun jwtService(): JwtService = jwtService
    override fun roleAuthorization(): RoleAuthorization = roleAuthorization
    override fun userService(): UserService = userService
    override fun authService(): AuthService = authService
    override fun courseService(): CourseService = courseService
    override fun attendanceService(): AttendanceService = attendanceService
    override fun assignmentService(): AssignmentService = assignmentService

    /**
     * Builder for ApplicationComponentImpl
     */
    class Builder {
        fun build(): ApplicationComponent {
            // Create all dependencies manually
            val databaseFactory = db.DatabaseFactory
            val jwtService = JwtService()
            val roleAuthorization = RoleAuthorizationImpl()

            // Create repositories
            val userRepository = features.users.repositories.UserRepositoryImpl()
            val studentRepository = features.users.repositories.StudentRepositoryImpl()
            val staffRepository = features.users.repositories.StaffRepositoryImpl()
            val authRepository = features.auth.repositories.AuthRepositoryImpl()
            val courseRepository = features.courses.repositories.CourseRepositoryImpl()
            val courseScheduleRepository = features.courses.repositories.CourseScheduleRepositoryImpl()
            val attendanceSessionRepository = features.attendance.repositories.AttendanceSessionRepositoryImpl()
            val attendanceRepository = features.attendance.repositories.AttendanceRepositoryImpl()
            val assignmentRepository = features.assignments.repositories.AssignmentRepositoryImpl()
            val submissionRepository = features.assignments.repositories.SubmissionRepositoryImpl()
            val gradeRepository = features.assignments.repositories.GradeRepositoryImpl()

            // Create services
            val userService = UserServiceImpl(userRepository, studentRepository, staffRepository)
            val authService = AuthServiceImpl(authRepository, userRepository, studentRepository, staffRepository, jwtService)
            val courseService = CourseServiceImpl(courseRepository, courseScheduleRepository, userRepository)
            val attendanceService = AttendanceServiceImpl(attendanceSessionRepository, attendanceRepository, courseRepository, userRepository)
            val assignmentService = AssignmentServiceImpl(assignmentRepository, submissionRepository, gradeRepository, courseRepository, userRepository)

            return ApplicationComponentImpl(
                databaseFactory,
                jwtService,
                roleAuthorization,
                userService,
                authService,
                courseService,
                attendanceService,
                assignmentService
            )
        }
    }
}