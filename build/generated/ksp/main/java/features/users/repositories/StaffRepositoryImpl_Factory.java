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
public final class StaffRepositoryImpl_Factory implements Factory<StaffRepositoryImpl> {
  @Override
  public StaffRepositoryImpl get() {
    return newInstance();
  }

  public static StaffRepositoryImpl_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static StaffRepositoryImpl newInstance() {
    return new StaffRepositoryImpl();
  }

  private static final class InstanceHolder {
    private static final StaffRepositoryImpl_Factory INSTANCE = new StaffRepositoryImpl_Factory();
  }
}
