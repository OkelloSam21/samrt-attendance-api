package features.attendance.services;

import com.example.features.attendance.services.AttendanceServiceImpl;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import com.example.features.attendance.repositories.AttendanceRepository;
import com.example.features.attendance.repositories.AttendanceSessionRepository;
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
public final class AttendanceServiceImpl_Factory implements Factory<AttendanceServiceImpl> {
  private final Provider<AttendanceSessionRepository> attendanceSessionRepositoryProvider;

  private final Provider<AttendanceRepository> attendanceRepositoryProvider;

  private final Provider<CourseRepository> courseRepositoryProvider;

  private final Provider<UserRepository> userRepositoryProvider;

  public AttendanceServiceImpl_Factory(
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
  public AttendanceServiceImpl get() {
    return newInstance(attendanceSessionRepositoryProvider.get(), attendanceRepositoryProvider.get(), courseRepositoryProvider.get(), userRepositoryProvider.get());
  }

  public static AttendanceServiceImpl_Factory create(
      Provider<AttendanceSessionRepository> attendanceSessionRepositoryProvider,
      Provider<AttendanceRepository> attendanceRepositoryProvider,
      Provider<CourseRepository> courseRepositoryProvider,
      Provider<UserRepository> userRepositoryProvider) {
    return new AttendanceServiceImpl_Factory(attendanceSessionRepositoryProvider, attendanceRepositoryProvider, courseRepositoryProvider, userRepositoryProvider);
  }

  public static AttendanceServiceImpl newInstance(
      AttendanceSessionRepository attendanceSessionRepository,
      AttendanceRepository attendanceRepository, CourseRepository courseRepository,
      UserRepository userRepository) {
    return new AttendanceServiceImpl(attendanceSessionRepository, attendanceRepository, courseRepository, userRepository);
  }
}
