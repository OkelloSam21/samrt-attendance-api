package features.attendance.repositories;

import com.example.features.attendance.repositories.AttendanceRepositoryImpl;
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
public final class AttendanceRepositoryImpl_Factory implements Factory<AttendanceRepositoryImpl> {
  @Override
  public AttendanceRepositoryImpl get() {
    return newInstance();
  }

  public static AttendanceRepositoryImpl_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static AttendanceRepositoryImpl newInstance() {
    return new AttendanceRepositoryImpl();
  }

  private static final class InstanceHolder {
    private static final AttendanceRepositoryImpl_Factory INSTANCE = new AttendanceRepositoryImpl_Factory();
  }
}
