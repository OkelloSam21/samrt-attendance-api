package di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import features.assignments.repositories.GradeRepository;
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
public final class AssignmentModule_ProvideGradeRepositoryFactory implements Factory<GradeRepository> {
  @Override
  public GradeRepository get() {
    return provideGradeRepository();
  }

  public static AssignmentModule_ProvideGradeRepositoryFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static GradeRepository provideGradeRepository() {
    return Preconditions.checkNotNullFromProvides(AssignmentModule.INSTANCE.provideGradeRepository());
  }

  private static final class InstanceHolder {
    private static final AssignmentModule_ProvideGradeRepositoryFactory INSTANCE = new AssignmentModule_ProvideGradeRepositoryFactory();
  }
}
