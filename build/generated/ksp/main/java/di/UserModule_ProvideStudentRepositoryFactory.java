package di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import features.users.repositories.StudentRepository;
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
public final class UserModule_ProvideStudentRepositoryFactory implements Factory<StudentRepository> {
  @Override
  public StudentRepository get() {
    return provideStudentRepository();
  }

  public static UserModule_ProvideStudentRepositoryFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static StudentRepository provideStudentRepository() {
    return Preconditions.checkNotNullFromProvides(UserModule.INSTANCE.provideStudentRepository());
  }

  private static final class InstanceHolder {
    private static final UserModule_ProvideStudentRepositoryFactory INSTANCE = new UserModule_ProvideStudentRepositoryFactory();
  }
}
