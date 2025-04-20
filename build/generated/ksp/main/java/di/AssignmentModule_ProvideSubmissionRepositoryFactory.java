package di;

import com.example.di.AssignmentModule;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import com.example.features.assignments.repositories.SubmissionRepository;
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
public final class AssignmentModule_ProvideSubmissionRepositoryFactory implements Factory<SubmissionRepository> {
  @Override
  public SubmissionRepository get() {
    return provideSubmissionRepository();
  }

  public static AssignmentModule_ProvideSubmissionRepositoryFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static SubmissionRepository provideSubmissionRepository() {
    return Preconditions.checkNotNullFromProvides(AssignmentModule.INSTANCE.provideSubmissionRepository());
  }

  private static final class InstanceHolder {
    private static final AssignmentModule_ProvideSubmissionRepositoryFactory INSTANCE = new AssignmentModule_ProvideSubmissionRepositoryFactory();
  }
}
