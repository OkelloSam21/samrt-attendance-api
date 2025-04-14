package di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import features.assignments.repositories.AssignmentRepository;
import features.assignments.repositories.GradeRepository;
import features.assignments.repositories.SubmissionRepository;
import features.assignments.services.AssignmentService;
import features.courses.repositories.CourseRepository;
import features.users.repositories.UserRepository;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class AssignmentModule_ProvideAssignmentServiceFactory implements Factory<AssignmentService> {
  private final Provider<AssignmentRepository> assignmentRepositoryProvider;

  private final Provider<CourseRepository> courseRepositoryProvider;

  private final Provider<UserRepository> userRepositoryProvider;

  private final Provider<SubmissionRepository> submissionRepositoryProvider;

  private final Provider<GradeRepository> gradeRepositoryProvider;

  public AssignmentModule_ProvideAssignmentServiceFactory(
      Provider<AssignmentRepository> assignmentRepositoryProvider,
      Provider<CourseRepository> courseRepositoryProvider,
      Provider<UserRepository> userRepositoryProvider,
      Provider<SubmissionRepository> submissionRepositoryProvider,
      Provider<GradeRepository> gradeRepositoryProvider) {
    this.assignmentRepositoryProvider = assignmentRepositoryProvider;
    this.courseRepositoryProvider = courseRepositoryProvider;
    this.userRepositoryProvider = userRepositoryProvider;
    this.submissionRepositoryProvider = submissionRepositoryProvider;
    this.gradeRepositoryProvider = gradeRepositoryProvider;
  }

  @Override
  public AssignmentService get() {
    return provideAssignmentService(assignmentRepositoryProvider.get(), courseRepositoryProvider.get(), userRepositoryProvider.get(), submissionRepositoryProvider.get(), gradeRepositoryProvider.get());
  }

  public static AssignmentModule_ProvideAssignmentServiceFactory create(
      Provider<AssignmentRepository> assignmentRepositoryProvider,
      Provider<CourseRepository> courseRepositoryProvider,
      Provider<UserRepository> userRepositoryProvider,
      Provider<SubmissionRepository> submissionRepositoryProvider,
      Provider<GradeRepository> gradeRepositoryProvider) {
    return new AssignmentModule_ProvideAssignmentServiceFactory(assignmentRepositoryProvider, courseRepositoryProvider, userRepositoryProvider, submissionRepositoryProvider, gradeRepositoryProvider);
  }

  public static AssignmentService provideAssignmentService(
      AssignmentRepository assignmentRepository, CourseRepository courseRepository,
      UserRepository userRepository, SubmissionRepository submissionRepository,
      GradeRepository gradeRepository) {
    return Preconditions.checkNotNullFromProvides(AssignmentModule.INSTANCE.provideAssignmentService(assignmentRepository, courseRepository, userRepository, submissionRepository, gradeRepository));
  }
}
