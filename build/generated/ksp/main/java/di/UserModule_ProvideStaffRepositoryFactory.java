package di;

import com.example.di.UserModule;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import com.example.features.users.repositories.StaffRepository;
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
public final class UserModule_ProvideStaffRepositoryFactory implements Factory<StaffRepository> {
  @Override
  public StaffRepository get() {
    return provideStaffRepository();
  }

  public static UserModule_ProvideStaffRepositoryFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static StaffRepository provideStaffRepository() {
    return Preconditions.checkNotNullFromProvides(UserModule.INSTANCE.provideStaffRepository());
  }

  private static final class InstanceHolder {
    private static final UserModule_ProvideStaffRepositoryFactory INSTANCE = new UserModule_ProvideStaffRepositoryFactory();
  }
}
