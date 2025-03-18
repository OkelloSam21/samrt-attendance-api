package util

import java.io.File
import java.util.*

object AppConfig {
    // JWT Configuration
    val jwtSecret: String
    val jwtIssuer: String
    val jwtAudience: String
    val jwtRealm: String

    // Database Configuration
    val dbUrl: String
    val dbUsername: String
    val dbPassword: String

    // App Configuration
    val isDevelopment: Boolean
    val port: Int


    init {
        println("Initializing application configuration...")

        // Load properties from local.properties file for development
        val properties = Properties()

        // Try to load local properties file (for development)
        val localPropertiesFile = File("local.properties")
        println("Checking for local.properties at: ${localPropertiesFile.absolutePath}")
        println("File exists: ${localPropertiesFile.exists()}")

        if (localPropertiesFile.exists()) {
            try {
                localPropertiesFile.inputStream().use {
                    properties.load(it)
                    println("Loaded local.properties configuration file")
                    println("Properties loaded: ${properties.size}")
                    println("Available keys: ${properties.keys.joinToString()}")
                }
            } catch (e: Exception) {
                println("Error loading properties file: ${e.message}")
                e.printStackTrace()
            }
        } else {
            println("No local.properties file found, using environment variables only")
        }

        // Environment determination (from env var or properties)
        isDevelopment = getConfigValue("environment", properties, "development") != "production"
        println("Running in ${if (isDevelopment) "DEVELOPMENT" else "PRODUCTION"} mode")

        // Get values from environment variables, then fallback to properties file
        jwtSecret = getConfigValue("JWT_SECRET", properties, "jwt.secret", required = !isDevelopment)
        jwtIssuer = getConfigValue("JWT_ISSUER", properties, "jwt.issuer", required = !isDevelopment)
        jwtAudience = getConfigValue("JWT_AUDIENCE", properties, "jwt.audience", required = !isDevelopment)
        jwtRealm = getConfigValue("JWT_REALM", properties, "jwt.realm", required = !isDevelopment)

        // Database config
        dbUrl = getConfigValue("DB_URL", properties, "db.url")
        dbUsername = getConfigValue("DB_USERNAME", properties, "db.username")
        dbPassword = getConfigValue("DB_PASSWORD", properties, "db.password")

        // Other configs
        port = getConfigValue("PORT", properties, "port", "8080").toInt()

        println("DEBUG - Database URL: $dbUrl")
        println("DEBUG - Database Username: $dbUsername")

        println("Configuration loaded successfully")
    }

    private fun getConfigValue(
        envKey: String,
        properties: Properties,
        propKey: String? = null,
        defaultValue: String? = null,
        required: Boolean = false
    ): String {
        // Try environment variable first
        val fromEnv = System.getenv(envKey)
        if (fromEnv != null) {
            return fromEnv
        }

        // Then try property file if propKey is provided
        if (propKey != null) {
            val fromProps = properties.getProperty(propKey)
            if (fromProps != null) {
                return fromProps
            }
        }

        // Fall back to default value
        if (defaultValue != null) {
            return defaultValue
        }

        // If required and no value found, throw exception
        if (required) {
            throw IllegalStateException("Required configuration value $envKey not found in environment variables or properties file")
        }

        // Otherwise return empty string
        return ""
    }
}