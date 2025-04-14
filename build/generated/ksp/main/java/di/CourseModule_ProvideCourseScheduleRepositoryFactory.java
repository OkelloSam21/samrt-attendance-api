package di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import features.courses.repositories.CourseScheduleRepository;
import javax.annotation.processing.Generated;

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
public final class CourseModule_ProvideCourseScheduleRepositoryFactory implements Factory<CourseScheduleRepository> {
  @Override
  public CourseScheduleRepository get() {
    return provideCourseScheduleRepository();
  }

  public static CourseModule_ProvideCourseScheduleRepositoryFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static CourseScheduleRepository provideCourseScheduleRepository() {
    return Preconditions.checkNotNullFromProvides(CourseModule.INSTANCE.provideCourseScheduleRepository());
  }

  private static final class InstanceHolder {
    private static final CourseModule_ProvideCourseScheduleRepositoryFactory INSTANCE = new CourseModule_ProvideCourseScheduleRepositoryFactory();
  }
}
