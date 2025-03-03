plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
}

group = "com.smart-attendance"
version = "0.0.1"

application {
    mainClass = "io.ktor.server.netty.EngineMain"

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")

}

repositories {
    mavenCentral()
}

tasks {
    shadowJar {
        archiveBaseName.set("smart-attendance-api")
        archiveClassifier.set("")
        archiveVersion.set("")
    }
}

dependencies {
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.logback.classic)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.config.yaml)
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.kotlin.test.junit)

    implementation(libs.ktor.server.content.negotiation.jvm)
    implementation(libs.ktor.kotlinx.serialization)

    implementation("org.jetbrains.exposed:exposed-core:0.43.0")
    implementation("org.jetbrains.exposed:exposed-dao:0.43.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.43.0")
    implementation("org.jetbrains.exposed:exposed-java-time:0.43.0") // For timestamps

    implementation("com.zaxxer:HikariCP:5.0.1")

    implementation("mysql:mysql-connector-java:8.0.33") // MySQL driver

    implementation("org.mindrot:jbcrypt:0.4")

    implementation("com.zaxxer:HikariCP:5.0.1")

    implementation("io.ktor:ktor-server-swagger:2.3.5")
    implementation("io.ktor:ktor-server-openapi:2.3.5")

    implementation("io.ktor:ktor-server-cors:2.3.5")


}