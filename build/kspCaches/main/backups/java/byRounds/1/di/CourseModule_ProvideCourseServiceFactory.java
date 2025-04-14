package di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import features.courses.repositories.CourseRepository;
import features.courses.repositories.CourseScheduleRepository;
import features.courses.services.CourseService;
import features.users.repositories.UserRepository;
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
public final class CourseModule_ProvideCourseServiceFactory implements Factory<CourseService> {
  private final Provider<CourseRepository> courseRepositoryProvider;

  private final Provider<CourseScheduleRepository> courseScheduleRepositoryProvider;

  private final Provider<UserRepository> userRepositoryProvider;

  public CourseModule_ProvideCourseServiceFactory(
      Provider<CourseRepository> courseRepositoryProvider,
      Provider<CourseScheduleRepository> courseScheduleRepositoryProvider,
      Provider<UserRepository> userRepositoryProvider) {
    this.courseRepositoryProvider = courseRepositoryProvider;
    this.courseScheduleRepositoryProvider = courseScheduleRepositoryProvider;
    this.userRepositoryProvider = userRepositoryProvider;
  }

  @Override
  public CourseService get() {
    return provideCourseService(courseRepositoryProvider.get(), courseScheduleRepositoryProvider.get(), userRepositoryProvider.get());
  }

  public static CourseModule_ProvideCourseServiceFactory create(
      Provider<CourseRepository> courseRepositoryProvider,
      Provider<CourseScheduleRepository> courseScheduleRepositoryProvider,
      Provider<UserRepository> userRepositoryProvider) {
    return new CourseModule_ProvideCourseServiceFactory(courseRepositoryProvider, courseScheduleRepositoryProvider, userRepositoryProvider);
  }

  public static CourseService provideCourseService(CourseRepository courseRepository,
      CourseScheduleRepository courseScheduleRepository, UserRepository userRepository) {
    return Preconditions.checkNotNullFromProvides(CourseModule.INSTANCE.provideCourseService(courseRepository, courseScheduleRepository, userRepository));
  }
}
