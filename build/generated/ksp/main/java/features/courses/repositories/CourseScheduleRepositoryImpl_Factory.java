package features.courses.repositories;

import com.example.features.courses.repositories.CourseScheduleRepositoryImpl;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
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
public final class CourseScheduleRepositoryImpl_Factory implements Factory<CourseScheduleRepositoryImpl> {
  @Override
  public CourseScheduleRepositoryImpl get() {
    return newInstance();
  }

  public static CourseScheduleRepositoryImpl_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static CourseScheduleRepositoryImpl newInstance() {
    return new CourseScheduleRepositoryImpl();
  }

  private static final class InstanceHolder {
    private static final CourseScheduleRepositoryImpl_Factory INSTANCE = new CourseScheduleRepositoryImpl_Factory();
  }
}
