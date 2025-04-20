package di;

import com.example.di.AttendanceModule;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import com.example.features.attendance.repositories.AttendanceRepository;
import com.example.features.attendance.repositories.AttendanceSessionRepository;
import com.example.features.attendance.services.AttendanceService;
import com.example.features.courses.repositories.CourseRepository;
import com.example.features.users.repositories.UserRepository;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
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
public final class AttendanceModule_ProvideAttendanceServiceFactory implements Factory<AttendanceService> {
  private final Provider<AttendanceSessionRepository> attendanceSessionRepositoryProvider;

  private final Provider<AttendanceRepository> attendanceRepositoryProvider;

  private final Provider<CourseRepository> courseRepositoryProvider;

  private final Provider<UserRepository> userRepositoryProvider;

  public AttendanceModule_ProvideAttendanceServiceFactory(
      Provider<AttendanceSessionRepository> attendanceSessionRepositoryProvider,
      Provider<AttendanceRepository> attendanceRepositoryProvider,
      Provider<CourseRepository> courseRepositoryProvider,
      Provider<UserRepository> userRepositoryProvider) {
    this.attendanceSessionRepositoryProvider = attendanceSessionRepositoryProvider;
    this.attendanceRepositoryProvider = attendanceRepositoryProvider;
    this.courseRepositoryProvider = courseRepositoryProvider;
    this.userRepositoryProvider = userRepositoryProvider;
  }

  @Override
  public AttendanceService get() {
    return provideAttendanceService(attendanceSessionRepositoryProvider.get(), attendanceRepositoryProvider.get(), courseRepositoryProvider.get(), userRepositoryProvider.get());
  }

  public static AttendanceModule_ProvideAttendanceServiceFactory create(
      Provider<AttendanceSessionRepository> attendanceSessionRepositoryProvider,
      Provider<AttendanceRepository> attendanceRepositoryProvider,
      Provider<CourseRepository> courseRepositoryProvider,
      Provider<UserRepository> userRepositoryProvider) {
    return new AttendanceModule_ProvideAttendanceServiceFactory(attendanceSessionRepositoryProvider, attendanceRepositoryProvider, courseRepositoryProvider, userRepositoryProvider);
  }

  public static AttendanceService provideAttendanceService(
      AttendanceSessionRepository attendanceSessionRepository,
      AttendanceRepository attendanceRepository, CourseRepository courseRepository,
      UserRepository userRepository) {
    return Preconditions.checkNotNullFromProvides(AttendanceModule.INSTANCE.provideAttendanceService(attendanceSessionRepository, attendanceRepository, courseRepository, userRepository));
  }
}
