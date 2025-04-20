package features.courses.repositories;

import com.example.features.courses.repositories.CourseRepositoryImpl;
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
public final class CourseRepositoryImpl_Factory implements Factory<CourseRepositoryImpl> {
  @Override
  public CourseRepositoryImpl get() {
    return newInstance();
  }

  public static CourseRepositoryImpl_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static CourseRepositoryImpl newInstance() {
    return new CourseRepositoryImpl();
  }

  private static final class InstanceHolder {
    private static final CourseRepositoryImpl_Factory INSTANCE = new CourseRepositoryImpl_Factory();
  }
}
