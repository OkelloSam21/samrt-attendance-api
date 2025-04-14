package features.auth.services;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import features.auth.repositories.AuthRepository;
import features.users.repositories.StaffRepository;
import features.users.repositories.StudentRepository;
import features.users.repositories.UserRepository;
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
public final class AuthServiceImpl_Factory implements Factory<AuthServiceImpl> {
  private final Provider<AuthRepository> authRepositoryProvider;

  private final Provider<UserRepository> userRepositoryProvider;

  private final Provider<StudentRepository> studentRepositoryProvider;

  private final Provider<StaffRepository> staffRepositoryProvider;

  private final Provider<JwtService> jwtServiceProvider;

  public AuthServiceImpl_Factory(Provider<AuthRepository> authRepositoryProvider,
      Provider<UserRepository> userRepositoryProvider,
      Provider<StudentRepository> studentRepositoryProvider,
      Provider<StaffRepository> staffRepositoryProvider, Provider<JwtService> jwtServiceProvider) {
    this.authRepositoryProvider = authRepositoryProvider;
    this.userRepositoryProvider = userRepositoryProvider;
    this.studentRepositoryProvider = studentRepositoryProvider;
    this.staffRepositoryProvider = staffRepositoryProvider;
    this.jwtServiceProvider = jwtServiceProvider;
  }

  @Override
  public AuthServiceImpl get() {
    return newInstance(authRepositoryProvider.get(), userRepositoryProvider.get(), studentRepositoryProvider.get(), staffRepositoryProvider.get(), jwtServiceProvider.get());
  }

  public static AuthServiceImpl_Factory create(Provider<AuthRepository> authRepositoryProvider,
      Provider<UserRepository> userRepositoryProvider,
      Provider<StudentRepository> studentRepositoryProvider,
      Provider<StaffRepository> staffRepositoryProvider, Provider<JwtService> jwtServiceProvider) {
    return new AuthServiceImpl_Factory(authRepositoryProvider, userRepositoryProvider, studentRepositoryProvider, staffRepositoryProvider, jwtServiceProvider);
  }

  public static AuthServiceImpl newInstance(AuthRepository authRepository,
      UserRepository userRepository, StudentRepository studentRepository,
      StaffRepository staffRepository, JwtService jwtService) {
    return new AuthServiceImpl(authRepository, userRepository, studentRepository, staffRepository, jwtService);
  }
}
