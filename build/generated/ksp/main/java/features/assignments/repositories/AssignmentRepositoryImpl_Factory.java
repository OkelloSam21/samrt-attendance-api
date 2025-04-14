package features.assignments.repositories;

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
public final class AssignmentRepositoryImpl_Factory implements Factory<AssignmentRepositoryImpl> {
  @Override
  public AssignmentRepositoryImpl get() {
    return newInstance();
  }

  public static AssignmentRepositoryImpl_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static AssignmentRepositoryImpl newInstance() {
    return new AssignmentRepositoryImpl();
  }

  private static final class InstanceHolder {
    private static final AssignmentRepositoryImpl_Factory INSTANCE = new AssignmentRepositoryImpl_Factory();
  }
}
