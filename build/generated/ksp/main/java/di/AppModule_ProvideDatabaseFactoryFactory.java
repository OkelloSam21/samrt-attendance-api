package di;

import com.example.di.AppModule;
import com.example.di.DatabaseFactory;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class AppModule_ProvideDatabaseFactoryFactory implements Factory<DatabaseFactory> {
  @Override
  public DatabaseFactory get() {
    return provideDatabaseFactory();
  }

  public static AppModule_ProvideDatabaseFactoryFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static DatabaseFactory provideDatabaseFactory() {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideDatabaseFactory());
  }

  private static final class InstanceHolder {
    private static final AppModule_ProvideDatabaseFactoryFactory INSTANCE = new AppModule_ProvideDatabaseFactoryFactory();
  }
}
