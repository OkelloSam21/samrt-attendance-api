package di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import features.auth.repositories.AuthRepository;
import features.auth.services.AuthService;
import features.auth.services.JwtService;
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
public final class AuthModule_ProvideAuthServiceFactory implements Factory<AuthService> {
  private final Provider<AuthRepository> authRepositoryProvider;

  private final Provider<UserRepository> userRepositoryProvider;

  private final Provider<StudentRepository> studentRepositoryProvider;

  private final Provider<StaffRepository> staffRepositoryProvider;

  private final Provider<JwtService> jwtServiceProvider;

  public AuthModule_ProvideAuthServiceFactory(Provider<AuthRepository> authRepositoryProvider,
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
  public AuthService get() {
    return provideAuthService(authRepositoryProvider.get(), userRepositoryProvider.get(), studentRepositoryProvider.get(), staffRepositoryProvider.get(), jwtServiceProvider.get());
  }

  public static AuthModule_ProvideAuthServiceFactory create(
      Provider<AuthRepository> authRepositoryProvider,
      Provider<UserRepository> userRepositoryProvider,
      Provider<StudentRepository> studentRepositoryProvider,
      Provider<StaffRepository> staffRepositoryProvider, Provider<JwtService> jwtServiceProvider) {
    return new AuthModule_ProvideAuthServiceFactory(authRepositoryProvider, userRepositoryProvider, studentRepositoryProvider, staffRepositoryProvider, jwtServiceProvider);
  }

  public static AuthService provideAuthService(AuthRepository authRepository,
      UserRepository userRepository, StudentRepository studentRepository,
      StaffRepository staffRepository, JwtService jwtService) {
    return Preconditions.checkNotNullFromProvides(AuthModule.INSTANCE.provideAuthService(authRepository, userRepository, studentRepository, staffRepository, jwtService));
  }
}
