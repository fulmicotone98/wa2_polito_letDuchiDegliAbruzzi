package wa2.polito.it.letduchidegliabruzzi.server.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain


@Configuration
@EnableWebSecurity
class WebSecurityConfig {

    val manager: String = "Manager"
    val expert: String = "Expert"
    val customer: String = "Client"

    private val jwtAuthConverter: JwtAuthConverter? = null

    @Bean
    @Throws(Exception::class)
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain? {
        http.authorizeHttpRequests()
            .requestMatchers(HttpMethod.GET, "/**").hasAnyRole(manager, expert, customer)
            //.requestMatchers(HttpMethod.GET, "/**").permitAll()
            //.requestMatchers(HttpMethod.GET, "/test/admin", "/test/admin/**").hasRole(admin)
            //.requestMatchers(HttpMethod.GET, "/test/user").hasAnyRole(admin, user)
            .anyRequest().authenticated()

        http.oauth2ResourceServer()
            .jwt()
            .jwtAuthenticationConverter(jwtAuthConverter)
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        return http.build()
    }

}