package auth

//import java.util.*
//
//object JwtConfig {
//    private const val secret = "your_secret_key"
//    private const val issuer = "com.example"
//    private const val audience = "com.example.audience"
//    private const val validityInMs = 36_000_00 * 10 // 10 hours
//
//    private val algorithm = Algorithm.HMAC256(secret)
//
//    val verifier = JWT.require(algorithm)
//        .withIssuer(issuer)
//        .withAudience(audience)
//        .build()
//
//    fun generateToken(username: String): String = JWT.create()
//        .withIssuer(issuer)
//        .withAudience(audience)
//        .withClaim("username", username)
//        .withExpiresAt(Date(System.currentTimeMillis() + validityInMs))
//        .sign(algorithm)
//}
//
//fun Application.configureSecurity() {
//    install(Authentication) {
//        jwt("auth-jwt") {
//            realm = "ktor app"
//            verifier(JwtConfig.verifier)
//            validate { credential ->
//                if (credential.payload.getClaim("username").asString().isNotEmpty())
//                    JWTPrincipal(credential.payload)
//                else
//                    null
//            }
//        }
//    }
//}