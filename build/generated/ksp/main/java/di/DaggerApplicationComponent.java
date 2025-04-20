package di;

import com.example.di.ApplicationComponent;
import com.example.di.DatabaseFactory;
import dagger.internal.DaggerGenerated;
import dagger.internal.DoubleCheck;
import com.example.features.assignments.repositories.AssignmentRepository;
import com.example.features.assignments.repositories.GradeRepository;
import com.example.features.assignments.repositories.SubmissionRepository;
import com.example.features.assignments.services.AssignmentService;
import com.example.features.attendance.repositories.AttendanceRepository;
import com.example.features.attendance.repositories.AttendanceSessionRepository;
import com.example.features.attendance.services.AttendanceService;
import com.example.features.auth.repositories.AuthRepository;
import com.example.features.auth.services.AuthService;
import com.example.features.auth.services.JwtService;
import com.example.features.auth.util.RoleAuthorization;
import com.example.features.courses.repositories.CourseRepository;
import com.example.features.courses.repositories.CourseScheduleRepository;
import com.example.features.courses.services.CourseService;
import com.example.features.users.repositories.StaffRepository;
import com.example.features.users.repositories.StudentRepository;
import com.example.features.users.repositories.UserRepository;
import com.example.features.users.services.UserService;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava"
})
public final class DaggerApplicationComponent {
  private DaggerApplicationComponent() {
  }

  public static ApplicationComponent.Builder builder() {
    return new Builder();
  }

  public static ApplicationComponent create() {
    return new Builder().build();
  }

  private static final class Builder implements ApplicationComponent.Builder {
    @Override
    public ApplicationComponent build() {
      return new ApplicationComponentImpl();
    }
  }

  private static final class ApplicationComponentImpl implements ApplicationComponent {
    private final ApplicationComponentImpl applicationComponentImpl = this;

    private Provider<DatabaseFactory> provideDatabaseFactoryProvider;

    private Provider<JwtService> provideJwtServiceProvider;

    private Provider<AuthRepository> provideAuthRepositoryProvider;

    private Provider<UserRepository> provideUserRepositoryProvider;

    private Provider<StudentRepository> provideStudentRepositoryProvider;

    private Provider<StaffRepository> provideStaffRepositoryProvider;

    private Provider<AuthService> provideAuthServiceProvider;

    private Provider<RoleAuthorization> provideRoleAuthorizationProvider;

    private Provider<UserService> provideUserServiceProvider;

    private Provider<CourseRepository> provideCourseRepositoryProvider;

    private Provider<CourseScheduleRepository> provideCourseScheduleRepositoryProvider;

    private Provider<CourseService> provideCourseServiceProvider;

    private Provider<AttendanceSessionRepository> provideAttendanceSessionRepositoryProvider;

    private Provider<AttendanceRepository> provideAttendanceRepositoryProvider;

    private Provider<AttendanceService> provideAttendanceServiceProvider;

    private Provider<AssignmentRepository> provideAssignmentRepositoryProvider;

    private Provider<SubmissionRepository> provideSubmissionRepositoryProvider;

    private Provider<GradeRepository> provideGradeRepositoryProvider;

    private Provider<AssignmentService> provideAssignmentServiceProvider;

    private ApplicationComponentImpl() {

      initialize();

    }

    @SuppressWarnings("unchecked")
    private void initialize() {
      this.provideDatabaseFactoryProvider = DoubleCheck.provider(AppModule_ProvideDatabaseFactoryFactory.create());
      this.provideJwtServiceProvider = DoubleCheck.provider(AppModule_ProvideJwtServiceFactory.create());
      this.provideAuthRepositoryProvider = DoubleCheck.provider(AuthModule_ProvideAuthRepositoryFactory.create());
      this.provideUserRepositoryProvider = DoubleCheck.provider(UserModule_ProvideUserRepositoryFactory.create());
      this.provideStudentRepositoryProvider = DoubleCheck.provider(UserModule_ProvideStudentRepositoryFactory.create());
      this.provideStaffRepositoryProvider = DoubleCheck.provider(UserModule_ProvideStaffRepositoryFactory.create());
      this.provideAuthServiceProvider = DoubleCheck.provider(AuthModule_ProvideAuthServiceFactory.create(provideAuthRepositoryProvider, provideUserRepositoryProvider, provideStudentRepositoryProvider, provideStaffRepositoryProvider, provideJwtServiceProvider));
      this.provideRoleAuthorizationProvider = DoubleCheck.provider(AppModule_ProvideRoleAuthorizationFactory.create());
      this.provideUserServiceProvider = DoubleCheck.provider(UserModule_ProvideUserServiceFactory.create(provideUserRepositoryProvider, provideStudentRepositoryProvider, provideStaffRepositoryProvider));
      this.provideCourseRepositoryProvider = DoubleCheck.provider(CourseModule_ProvideCourseRepositoryFactory.create());
      this.provideCourseScheduleRepositoryProvider = DoubleCheck.provider(CourseModule_ProvideCourseScheduleRepositoryFactory.create());
      this.provideCourseServiceProvider = DoubleCheck.provider(CourseModule_ProvideCourseServiceFactory.create(provideCourseRepositoryProvider, provideCourseScheduleRepositoryProvider, provideUserRepositoryProvider));
      this.provideAttendanceSessionRepositoryProvider = DoubleCheck.provider(AttendanceModule_ProvideAttendanceSessionRepositoryFactory.create());
      this.provideAttendanceRepositoryProvider = DoubleCheck.provider(AttendanceModule_ProvideAttendanceRepositoryFactory.create());
      this.provideAttendanceServiceProvider = DoubleCheck.provider(AttendanceModule_ProvideAttendanceServiceFactory.create(provideAttendanceSessionRepositoryProvider, provideAttendanceRepositoryProvider, provideCourseRepositoryProvider, provideUserRepositoryProvider));
      this.provideAssignmentRepositoryProvider = DoubleCheck.provider(AssignmentModule_ProvideAssignmentRepositoryFactory.create());
      this.provideSubmissionRepositoryProvider = DoubleCheck.provider(AssignmentModule_ProvideSubmissionRepositoryFactory.create());
      this.provideGradeRepositoryProvider = DoubleCheck.provider(AssignmentModule_ProvideGradeRepositoryFactory.create());
      this.provideAssignmentServiceProvider = DoubleCheck.provider(AssignmentModule_ProvideAssignmentServiceFactory.create(provideAssignmentRepositoryProvider, provideCourseRepositoryProvider, provideUserRepositoryProvider, provideSubmissionRepositoryProvider, provideGradeRepositoryProvider));
    }

    @Override
    public DatabaseFactory databaseFactory() {
      return provideDatabaseFactoryProvider.get();
    }

    @Override
    public JwtService jwtService() {
      return provideJwtServiceProvider.get();
    }

    @Override
    public AuthService authService() {
      return provideAuthServiceProvider.get();
    }

    @Override
    public RoleAuthorization roleAuthorization() {
      return provideRoleAuthorizationProvider.get();
    }

    @Override
    public UserService userService() {
      return provideUserServiceProvider.get();
    }

    @Override
    public CourseService courseService() {
      return provideCourseServiceProvider.get();
    }

    @Override
    public AttendanceService attendanceService() {
      return provideAttendanceServiceProvider.get();
    }

    @Override
    public AssignmentService assignmentService() {
      return provideAssignmentServiceProvider.get();
    }
  }
}
