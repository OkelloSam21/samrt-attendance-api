package db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import models.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import common.util.AppConfig

object DatabaseFactory {
    fun init() {
        println("Initializing database...")

        try {
            val dataSource = hikari()
            println("HikariCP datasource created")

            Database.connect(dataSource)
            println("Database connection established")

            // Create tables in a transaction
            transaction {
                println("Creating database schema...")
                SchemaUtils.create(
                    Users,Students, Staff, Courses, Attendance, Assignments,
                    Submissions, Lectures, Notifications, Grades
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

        try {
            Class.forName("com.mysql.cj.jdbc.Driver")
            println("MySQL JDBC driver loaded successfully")
        } catch (e: ClassNotFoundException) {
            println("ERROR: MySQL JDBC driver not found!")
            e.printStackTrace()
        }

        val config = HikariConfig().apply {
            driverClassName = "com.mysql.cj.jdbc.Driver"
            jdbcUrl = AppConfig.dbUrl
            username = AppConfig.dbUsername
            password = AppConfig.dbPassword
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