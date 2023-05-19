package wa2.polito.it.letduchidegliabruzzi.server.security

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.validation.annotation.Validated



@Validated
@Configuration
@ConfigurationProperties(prefix = "jwt.auth.converter")
class JwtAuthConverterProperties {

    lateinit var resourceID: String
    lateinit var principalAttribute: String

}