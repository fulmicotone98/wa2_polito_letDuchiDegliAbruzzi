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
            .requestMatchers(HttpMethod.PUT, "API/ticket/**/assign").hasRole(manager)
            .requestMatchers(HttpMethod.PUT, "API/ticket/**/status").hasAnyRole(manager, expert)
            .requestMatchers(HttpMethod.GET, "API/ticket/**").hasAnyRole(manager, expert, customer)
            .requestMatchers(HttpMethod.POST, "API/ticket").hasAnyRole(manager, expert, customer)
            .requestMatchers(HttpMethod.GET, "API/products/**").hasAnyRole(manager, expert, customer)
            .requestMatchers(HttpMethod.GET, "API/products").hasAnyRole(manager, expert)
            .requestMatchers(HttpMethod.POST, "API/products").hasAnyRole(manager, expert, customer)
            .requestMatchers(HttpMethod.POST, "API/employee").hasRole(manager)
            .requestMatchers(HttpMethod.GET, "API/employees/**").hasAnyRole(manager, expert, customer)
            .requestMatchers(HttpMethod.GET, "/API/profile/**/tickets").hasAnyRole(manager, expert, customer)
            .requestMatchers(HttpMethod.GET, "/API/profiles/**").hasAnyRole(manager, expert, customer)
            .requestMatchers(HttpMethod.POST, "API/profiles").hasAnyRole(manager, customer)
            .requestMatchers(HttpMethod.PUT, "/API/profiles/**").hasAnyRole(manager, customer)
            .requestMatchers(HttpMethod.POST, "API/login").permitAll()
            .anyRequest().authenticated()

        http.oauth2ResourceServer()
            .jwt()
            .jwtAuthenticationConverter(jwtAuthConverter)
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        return http.build()
    }

}