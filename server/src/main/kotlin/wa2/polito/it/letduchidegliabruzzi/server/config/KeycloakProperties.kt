package wa2.polito.it.letduchidegliabruzzi.server.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "spring.keycloak")
data class KeycloakProperties(
    var serverUri: String = "http://localhost:8080",
    var username: String = "administrator",
    var password: String = "admin",
)