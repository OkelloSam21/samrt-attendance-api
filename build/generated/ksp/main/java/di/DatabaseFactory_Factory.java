package di;

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
public final class DatabaseFactory_Factory implements Factory<DatabaseFactory> {
  @Override
  public DatabaseFactory get() {
    return newInstance();
  }

  public static DatabaseFactory_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static DatabaseFactory newInstance() {
    return new DatabaseFactory();
  }

  private static final class InstanceHolder {
    private static final DatabaseFactory_Factory INSTANCE = new DatabaseFactory_Factory();
  }
}
