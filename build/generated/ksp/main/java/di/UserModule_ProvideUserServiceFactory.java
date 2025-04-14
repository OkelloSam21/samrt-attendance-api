package di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import features.users.repositories.StaffRepository;
import features.users.repositories.StudentRepository;
import features.users.repositories.UserRepository;
import features.users.services.UserService;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class UserModule_ProvideUserServiceFactory implements Factory<UserService> {
  private final Provider<UserRepository> userRepositoryProvider;

  private final Provider<StudentRepository> studentRepositoryProvider;

  private final Provider<StaffRepository> staffRepositoryProvider;

  public UserModule_ProvideUserServiceFactory(Provider<UserRepository> userRepositoryProvider,
      Provider<StudentRepository> studentRepositoryProvider,
      Provider<StaffRepository> staffRepositoryProvider) {
    this.userRepositoryProvider = userRepositoryProvider;
    this.studentRepositoryProvider = studentRepositoryProvider;
    this.staffRepositoryProvider = staffRepositoryProvider;
  }

  @Override
  public UserService get() {
    return provideUserService(userRepositoryProvider.get(), studentRepositoryProvider.get(), staffRepositoryProvider.get());
  }

  public static UserModule_ProvideUserServiceFactory create(
      Provider<UserRepository> userRepositoryProvider,
      Provider<StudentRepository> studentRepositoryProvider,
      Provider<StaffRepository> staffRepositoryProvider) {
    return new UserModule_ProvideUserServiceFactory(userRepositoryProvider, studentRepositoryProvider, staffRepositoryProvider);
  }

  public static UserService provideUserService(UserRepository userRepository,
      StudentRepository studentRepository, StaffRepository staffRepository) {
    return Preconditions.checkNotNullFromProvides(UserModule.INSTANCE.provideUserService(userRepository, studentRepository, staffRepository));
  }
}
