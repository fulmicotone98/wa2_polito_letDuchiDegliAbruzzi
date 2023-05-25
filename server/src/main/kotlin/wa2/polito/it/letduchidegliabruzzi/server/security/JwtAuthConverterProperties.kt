package wa2.polito.it.letduchidegliabruzzi.server.security

import org.springframework.context.annotation.Configuration
import org.springframework.validation.annotation.Validated

@Validated
@Configuration
data class JwtAuthConverterProperties (
    var resourceID: String="springboot-keycloak-client",
    var principalAttribute: String="preferred_username"
)