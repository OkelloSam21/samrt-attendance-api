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
public final class SubmissionRepositoryImpl_Factory implements Factory<SubmissionRepositoryImpl> {
  @Override
  public SubmissionRepositoryImpl get() {
    return newInstance();
  }

  public static SubmissionRepositoryImpl_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static SubmissionRepositoryImpl newInstance() {
    return new SubmissionRepositoryImpl();
  }

  private static final class InstanceHolder {
    private static final SubmissionRepositoryImpl_Factory INSTANCE = new SubmissionRepositoryImpl_Factory();
  }
}
