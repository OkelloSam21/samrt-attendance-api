package di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import features.attendance.repositories.AttendanceRepository;
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
public final class AttendanceModule_ProvideAttendanceRepositoryFactory implements Factory<AttendanceRepository> {
  @Override
  public AttendanceRepository get() {
    return provideAttendanceRepository();
  }

  public static AttendanceModule_ProvideAttendanceRepositoryFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static AttendanceRepository provideAttendanceRepository() {
    return Preconditions.checkNotNullFromProvides(AttendanceModule.INSTANCE.provideAttendanceRepository());
  }

  private static final class InstanceHolder {
    private static final AttendanceModule_ProvideAttendanceRepositoryFactory INSTANCE = new AttendanceModule_ProvideAttendanceRepositoryFactory();
  }
}
