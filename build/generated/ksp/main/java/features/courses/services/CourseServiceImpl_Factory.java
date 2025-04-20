package features.courses.services;

import com.example.features.courses.services.CourseServiceImpl;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import com.example.features.courses.repositories.CourseRepository;
import com.example.features.courses.repositories.CourseScheduleRepository;
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
public final class CourseServiceImpl_Factory implements Factory<CourseServiceImpl> {
  private final Provider<CourseRepository> courseRepositoryProvider;

  private final Provider<CourseScheduleRepository> courseScheduleRepositoryProvider;

  private final Provider<UserRepository> userRepositoryProvider;

  public CourseServiceImpl_Factory(Provider<CourseRepository> courseRepositoryProvider,
      Provider<CourseScheduleRepository> courseScheduleRepositoryProvider,
      Provider<UserRepository> userRepositoryProvider) {
    this.courseRepositoryProvider = courseRepositoryProvider;
    this.courseScheduleRepositoryProvider = courseScheduleRepositoryProvider;
    this.userRepositoryProvider = userRepositoryProvider;
  }

  @Override
  public CourseServiceImpl get() {
    return newInstance(courseRepositoryProvider.get(), courseScheduleRepositoryProvider.get(), userRepositoryProvider.get());
  }

  public static CourseServiceImpl_Factory create(
      Provider<CourseRepository> courseRepositoryProvider,
      Provider<CourseScheduleRepository> courseScheduleRepositoryProvider,
      Provider<UserRepository> userRepositoryProvider) {
    return new CourseServiceImpl_Factory(courseRepositoryProvider, courseScheduleRepositoryProvider, userRepositoryProvider);
  }

  public static CourseServiceImpl newInstance(CourseRepository courseRepository,
      CourseScheduleRepository courseScheduleRepository, UserRepository userRepository) {
    return new CourseServiceImpl(courseRepository, courseScheduleRepository, userRepository);
  }
}
