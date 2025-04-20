package features.auth.services;

import com.example.features.auth.services.JwtService;
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
public final class JwtService_Factory implements Factory<JwtService> {
  @Override
  public JwtService get() {
    return newInstance();
  }

  public static JwtService_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static JwtService newInstance() {
    return new JwtService();
  }

  private static final class InstanceHolder {
    private static final JwtService_Factory INSTANCE = new JwtService_Factory();
  }
}
