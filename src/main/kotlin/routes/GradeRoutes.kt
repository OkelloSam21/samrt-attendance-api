//package routes
//
//import io.ktor.server.request.*
//import io.ktor.server.routing.*
//import models.Grades
//import org.jetbrains.exposed.sql.*
//import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
//import org.jetbrains.exposed.sql.transactions.transaction
//import java.time.Instant
//import java.common.util.*
//
//fun Route.gradeRoutes() {
//    route("/grades") {
//
//        // GET ALL GRADES
//        get {
//            val grades = transaction {
//                Grades.selectAll().map {
//                    mapOf(
//                        "id" to it[Grades.id].toString(),
//                        "student_id" to it[Grades.studentId].toString(),
//                        "assignment_id" to it[Grades.assignmentId].toString(),
//                        "score" to it[Grades.score],
//                        "feedback" to it[Grades.feedback],
//                        "graded_at" to it[Grades.gradedAt].toString()
//                    )
//                }
//            }
//            call.respond(
//                mapOf("message" to "grade successfully fetched", "grade" to grades.toString()),
//                typeInfo = TODO()
//            )
//        }
//
//        // GET SINGLE GRADE
//        get("/{id}") {
//            val id = call.parameters["id"] ?: return@get call.respond(mapOf("error" to "ID required"))
//
//            val grade = transaction {
//                Grades.select { Grades.id eq UUID.fromString(id) }
//                    .map {
//                        mapOf(
//                            "id" to it[Grades.id].toString(),
//                            "student_id" to it[Grades.studentId].toString(),
//                            "assignment_id" to it[Grades.assignmentId].toString(),
//                            "score" to it[Grades.score],
//                            "feedback" to it[Grades.feedback],
//                            "graded_at" to it[Grades.gradedAt].toString()
//                        )
//                    }.singleOrNull()
//            }
//            if (grade != null) call.respond(grade) else call.respond(mapOf("error" to "Grade not found"))
//        }
//
//        // CREATE GRADE
//        post {
//            val request = call.receive<Map<String, String>>()
//            val studentId =
//                request["student_id"] ?: return@post call.respond(mapOf("error" to "Student ID is required"))
//            val assignmentId =
//                request["assignment_id"] ?: return@post call.respond(mapOf("error" to "Assignment ID is required"))
//            val score = request["score"]?.toFloatOrNull()
//                ?: return@post call.respond(mapOf("error" to "Valid score is required"))
//            val feedback = request["feedback"]
//            val gradedAt = Instant.now()
//
//            val gradeId = UUID.randomUUID()
//
//            transaction {
//                Grades.insert {
//                    it[id] = gradeId
//                    it[Grades.studentId] = UUID.fromString(studentId)
//                    it[Grades.assignmentId] = UUID.fromString(assignmentId)
//                    it[Grades.score] = score
//                    it[Grades.feedback] = feedback
//                    it[Grades.gradedAt] = gradedAt
//                }
//            }
//
//            call.respond(mapOf("message" to "Grade recorded successfully", "gradeId" to gradeId))
//        }
//
//        // UPDATE GRADE
//        put("/{id}") {
//            val id = call.parameters["id"] ?: return@put call.respond(mapOf("error" to "ID required"))
//            val request = call.receive<Map<String, String>>()
//            val score = request["score"]?.toFloatOrNull()
//            val feedback = request["feedback"]
//
//            transaction {
//                Grades.update({ Grades.id eq UUID.fromString(id) }) {
//                    if (score != null) it[Grades.score] = score
//                    if (feedback != null) it[Grades.feedback] = feedback
//                }
//            }
//            call.respond(mapOf("message" to "Grade updated successfully"))
//        }
//
//        // DELETE GRADE
//        delete("/{id}") {
//            val id = call.parameters["id"] ?: return@delete call.respond(mapOf("error" to "ID required"))
//
//            transaction {
//                Grades.deleteWhere { Grades.id eq UUID.fromString(id) }
//            }
//            call.respond(mapOf("message" to "Grade deleted successfully"))
//        }
//    }
//
//}
