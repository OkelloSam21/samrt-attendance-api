package features.users.services;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
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
public final class UserServiceImpl_Factory implements Factory<UserServiceImpl> {
  private final Provider<UserRepository> userRepositoryProvider;

  private final Provider<StudentRepository> studentRepositoryProvider;

  private final Provider<StaffRepository> staffRepositoryProvider;

  public UserServiceImpl_Factory(Provider<UserRepository> userRepositoryProvider,
      Provider<StudentRepository> studentRepositoryProvider,
      Provider<StaffRepository> staffRepositoryProvider) {
    this.userRepositoryProvider = userRepositoryProvider;
    this.studentRepositoryProvider = studentRepositoryProvider;
    this.staffRepositoryProvider = staffRepositoryProvider;
  }

  @Override
  public UserServiceImpl get() {
    return newInstance(userRepositoryProvider.get(), studentRepositoryProvider.get(), staffRepositoryProvider.get());
  }

  public static UserServiceImpl_Factory create(Provider<UserRepository> userRepositoryProvider,
      Provider<StudentRepository> studentRepositoryProvider,
      Provider<StaffRepository> staffRepositoryProvider) {
    return new UserServiceImpl_Factory(userRepositoryProvider, studentRepositoryProvider, staffRepositoryProvider);
  }

  public static UserServiceImpl newInstance(UserRepository userRepository,
      StudentRepository studentRepository, StaffRepository staffRepository) {
    return new UserServiceImpl(userRepository, studentRepository, staffRepository);
  }
}
