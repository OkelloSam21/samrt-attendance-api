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
public final class GradeRepositoryImpl_Factory implements Factory<GradeRepositoryImpl> {
  @Override
  public GradeRepositoryImpl get() {
    return newInstance();
  }

  public static GradeRepositoryImpl_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static GradeRepositoryImpl newInstance() {
    return new GradeRepositoryImpl();
  }

  private static final class InstanceHolder {
    private static final GradeRepositoryImpl_Factory INSTANCE = new GradeRepositoryImpl_Factory();
  }
}
