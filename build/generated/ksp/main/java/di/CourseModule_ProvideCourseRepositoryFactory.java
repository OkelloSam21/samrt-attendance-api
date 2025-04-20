package di;

import com.example.di.CourseModule;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import com.example.features.courses.repositories.CourseRepository;
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
public final class CourseModule_ProvideCourseRepositoryFactory implements Factory<CourseRepository> {
  @Override
  public CourseRepository get() {
    return provideCourseRepository();
  }

  public static CourseModule_ProvideCourseRepositoryFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static CourseRepository provideCourseRepository() {
    return Preconditions.checkNotNullFromProvides(CourseModule.INSTANCE.provideCourseRepository());
  }

  private static final class InstanceHolder {
    private static final CourseModule_ProvideCourseRepositoryFactory INSTANCE = new CourseModule_ProvideCourseRepositoryFactory();
  }
}
