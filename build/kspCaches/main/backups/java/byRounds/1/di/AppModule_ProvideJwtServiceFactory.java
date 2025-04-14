package di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import features.auth.services.JwtService;
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
public final class AppModule_ProvideJwtServiceFactory implements Factory<JwtService> {
  @Override
  public JwtService get() {
    return provideJwtService();
  }

  public static AppModule_ProvideJwtServiceFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static JwtService provideJwtService() {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideJwtService());
  }

  private static final class InstanceHolder {
    private static final AppModule_ProvideJwtServiceFactory INSTANCE = new AppModule_ProvideJwtServiceFactory();
  }
}
