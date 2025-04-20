package features.assignments.services;

import com.example.features.assignments.services.AssignmentServiceImpl;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import com.example.features.assignments.repositories.AssignmentRepository;
import com.example.features.assignments.repositories.GradeRepository;
import com.example.features.assignments.repositories.SubmissionRepository;
import com.example.features.courses.repositories.CourseRepository;
import com.example.features.users.repositories.UserRepository;
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
public final class AssignmentServiceImpl_Factory implements Factory<AssignmentServiceImpl> {
  private final Provider<AssignmentRepository> assignmentRepositoryProvider;

  private final Provider<SubmissionRepository> submissionRepositoryProvider;

  private final Provider<GradeRepository> gradeRepositoryProvider;

  private final Provider<CourseRepository> courseRepositoryProvider;

  private final Provider<UserRepository> userRepositoryProvider;

  public AssignmentServiceImpl_Factory(Provider<AssignmentRepository> assignmentRepositoryProvider,
      Provider<SubmissionRepository> submissionRepositoryProvider,
      Provider<GradeRepository> gradeRepositoryProvider,
      Provider<CourseRepository> courseRepositoryProvider,
      Provider<UserRepository> userRepositoryProvider) {
    this.assignmentRepositoryProvider = assignmentRepositoryProvider;
    this.submissionRepositoryProvider = submissionRepositoryProvider;
    this.gradeRepositoryProvider = gradeRepositoryProvider;
    this.courseRepositoryProvider = courseRepositoryProvider;
    this.userRepositoryProvider = userRepositoryProvider;
  }

  @Override
  public AssignmentServiceImpl get() {
    return newInstance(assignmentRepositoryProvider.get(), submissionRepositoryProvider.get(), gradeRepositoryProvider.get(), courseRepositoryProvider.get(), userRepositoryProvider.get());
  }

  public static AssignmentServiceImpl_Factory create(
      Provider<AssignmentRepository> assignmentRepositoryProvider,
      Provider<SubmissionRepository> submissionRepositoryProvider,
      Provider<GradeRepository> gradeRepositoryProvider,
      Provider<CourseRepository> courseRepositoryProvider,
      Provider<UserRepository> userRepositoryProvider) {
    return new AssignmentServiceImpl_Factory(assignmentRepositoryProvider, submissionRepositoryProvider, gradeRepositoryProvider, courseRepositoryProvider, userRepositoryProvider);
  }

  public static AssignmentServiceImpl newInstance(AssignmentRepository assignmentRepository,
      SubmissionRepository submissionRepository, GradeRepository gradeRepository,
      CourseRepository courseRepository, UserRepository userRepository) {
    return new AssignmentServiceImpl(assignmentRepository, submissionRepository, gradeRepository, courseRepository, userRepository);
  }
}
