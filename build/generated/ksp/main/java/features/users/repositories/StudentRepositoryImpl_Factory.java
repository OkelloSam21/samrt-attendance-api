package features.users.repositories;

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
public final class StudentRepositoryImpl_Factory implements Factory<StudentRepositoryImpl> {
  @Override
  public StudentRepositoryImpl get() {
    return newInstance();
  }

  public static StudentRepositoryImpl_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static StudentRepositoryImpl newInstance() {
    return new StudentRepositoryImpl();
  }

  private static final class InstanceHolder {
    private static final StudentRepositoryImpl_Factory INSTANCE = new StudentRepositoryImpl_Factory();
  }
}
