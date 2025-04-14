package di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import features.attendance.repositories.AttendanceSessionRepository;
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
public final class AttendanceModule_ProvideAttendanceSessionRepositoryFactory implements Factory<AttendanceSessionRepository> {
  @Override
  public AttendanceSessionRepository get() {
    return provideAttendanceSessionRepository();
  }

  public static AttendanceModule_ProvideAttendanceSessionRepositoryFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static AttendanceSessionRepository provideAttendanceSessionRepository() {
    return Preconditions.checkNotNullFromProvides(AttendanceModule.INSTANCE.provideAttendanceSessionRepository());
  }

  private static final class InstanceHolder {
    private static final AttendanceModule_ProvideAttendanceSessionRepositoryFactory INSTANCE = new AttendanceModule_ProvideAttendanceSessionRepositoryFactory();
  }
}
