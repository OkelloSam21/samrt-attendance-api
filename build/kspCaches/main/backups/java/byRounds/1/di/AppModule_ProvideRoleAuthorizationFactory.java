package di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import features.auth.util.RoleAuthorization;
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
public final class AppModule_ProvideRoleAuthorizationFactory implements Factory<RoleAuthorization> {
  @Override
  public RoleAuthorization get() {
    return provideRoleAuthorization();
  }

  public static AppModule_ProvideRoleAuthorizationFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static RoleAuthorization provideRoleAuthorization() {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideRoleAuthorization());
  }

  private static final class InstanceHolder {
    private static final AppModule_ProvideRoleAuthorizationFactory INSTANCE = new AppModule_ProvideRoleAuthorizationFactory();
  }
}
