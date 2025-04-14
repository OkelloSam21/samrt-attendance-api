package di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import features.users.repositories.UserRepository;
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
public final class UserModule_ProvideUserRepositoryFactory implements Factory<UserRepository> {
  @Override
  public UserRepository get() {
    return provideUserRepository();
  }

  public static UserModule_ProvideUserRepositoryFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static UserRepository provideUserRepository() {
    return Preconditions.checkNotNullFromProvides(UserModule.INSTANCE.provideUserRepository());
  }

  private static final class InstanceHolder {
    private static final UserModule_ProvideUserRepositoryFactory INSTANCE = new UserModule_ProvideUserRepositoryFactory();
  }
}
