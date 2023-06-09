package wa2.polito.it.letduchidegliabruzzi.server.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "loki")
data class LokiProperties(
    var url: String=""
)