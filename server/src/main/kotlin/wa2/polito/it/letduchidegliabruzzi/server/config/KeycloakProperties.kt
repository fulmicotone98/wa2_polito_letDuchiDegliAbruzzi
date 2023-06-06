package wa2.polito.it.letduchidegliabruzzi.server.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.bind.ConstructorBinding
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "keycloak")
data class KeycloakProperties(
    var serverUri: String = "",
    var username: String = "",
    var password: String = "",
    var adminCliSecret: String = ""
)