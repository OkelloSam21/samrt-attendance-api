package db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import models.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun init() {
        println("Initializing database...")

        try {
            // Explicitly initialize the database connection
            val dataSource = hikari()
            println("HikariCP datasource created")

            // Establish the database connection
            Database.connect(dataSource)
            println("Database connection established")

            // Create tables in a transaction
            transaction {
                println("Creating database schema...")
                SchemaUtils.create(
                    Users,
                    Courses,
                    Attendance,
                    Assignments,
                    Submissions,
                    Lectures,
                    Notifications,
                    Grades
                )
                println("Schema created successfully")
            }
        } catch (e: Exception) {
            println("Database initialization failed: ${e.message}")
            e.printStackTrace()
            throw e
        }
    }

    private fun hikari(): HikariDataSource {
        println("Configuring HikariCP...")

        // Log debug info about JDBC driver
        try {
            Class.forName("com.mysql.cj.jdbc.Driver")
            println("MySQL JDBC driver loaded successfully")
        } catch (e: ClassNotFoundException) {
            println("ERROR: MySQL JDBC driver not found!")
            e.printStackTrace()
        }

        val config = HikariConfig().apply {
            driverClassName = "com.mysql.cj.jdbc.Driver"
            jdbcUrl = System.getenv("DB_URL") ?: "jdbc:mysql://localhost:3306/smart_attendance?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
            username = System.getenv("DB_USERNAME") ?: "root"
            password = System.getenv("DB_PASSWORD") ?: "rootpassword"
            maximumPoolSize = 10
            minimumIdle = 5
            idleTimeout = 30000
            connectionTimeout = 30000
            maxLifetime = 1800000
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"

            connectionTestQuery = "SELECT 1"
            validationTimeout = 5000
        }
        println("HikariCP configuration complete")
        return HikariDataSource(config)
    }
}