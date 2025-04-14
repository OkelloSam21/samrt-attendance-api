package di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import features.assignments.repositories.AssignmentRepository;
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
public final class AssignmentModule_ProvideAssignmentRepositoryFactory implements Factory<AssignmentRepository> {
  @Override
  public AssignmentRepository get() {
    return provideAssignmentRepository();
  }

  public static AssignmentModule_ProvideAssignmentRepositoryFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static AssignmentRepository provideAssignmentRepository() {
    return Preconditions.checkNotNullFromProvides(AssignmentModule.INSTANCE.provideAssignmentRepository());
  }

  private static final class InstanceHolder {
    private static final AssignmentModule_ProvideAssignmentRepositoryFactory INSTANCE = new AssignmentModule_ProvideAssignmentRepositoryFactory();
  }
}
