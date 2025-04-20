package di;

import com.example.di.AuthModule;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import com.example.features.auth.repositories.AuthRepository;
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
public final class AuthModule_ProvideAuthRepositoryFactory implements Factory<AuthRepository> {
  @Override
  public AuthRepository get() {
    return provideAuthRepository();
  }

  public static AuthModule_ProvideAuthRepositoryFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static AuthRepository provideAuthRepository() {
    return Preconditions.checkNotNullFromProvides(AuthModule.INSTANCE.provideAuthRepository());
  }

  private static final class InstanceHolder {
    private static final AuthModule_ProvideAuthRepositoryFactory INSTANCE = new AuthModule_ProvideAuthRepositoryFactory();
  }
}
