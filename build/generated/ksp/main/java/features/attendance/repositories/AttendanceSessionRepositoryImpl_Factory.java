package features.attendance.repositories;

import com.example.features.attendance.repositories.AttendanceSessionRepositoryImpl;
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
public final class AttendanceSessionRepositoryImpl_Factory implements Factory<AttendanceSessionRepositoryImpl> {
  @Override
  public AttendanceSessionRepositoryImpl get() {
    return newInstance();
  }

  public static AttendanceSessionRepositoryImpl_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static AttendanceSessionRepositoryImpl newInstance() {
    return new AttendanceSessionRepositoryImpl();
  }

  private static final class InstanceHolder {
    private static final AttendanceSessionRepositoryImpl_Factory INSTANCE = new AttendanceSessionRepositoryImpl_Factory();
  }
}
