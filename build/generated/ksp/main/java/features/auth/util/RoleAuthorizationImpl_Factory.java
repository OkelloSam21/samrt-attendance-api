package features.auth.util;

import com.example.features.auth.util.RoleAuthorizationImpl;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
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
public final class RoleAuthorizationImpl_Factory implements Factory<RoleAuthorizationImpl> {
  @Override
  public RoleAuthorizationImpl get() {
    return newInstance();
  }

  public static RoleAuthorizationImpl_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static RoleAuthorizationImpl newInstance() {
    return new RoleAuthorizationImpl();
  }

  private static final class InstanceHolder {
    private static final RoleAuthorizationImpl_Factory INSTANCE = new RoleAuthorizationImpl_Factory();
  }
}
