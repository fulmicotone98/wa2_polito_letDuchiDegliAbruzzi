package wa2.polito.it.letduchidegliabruzzi.server.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import wa2.polito.it.letduchidegliabruzzi.server.security.JwtAuthConverter


@Configuration
@EnableWebSecurity
class WebSecurityConfig {

    val manager: String = "Manager"
    val expert: String = "Expert"
    val customer: String = "Client"

    val jwtAuthConverter : JwtAuthConverter = JwtAuthConverter()

    @Bean
    @Throws(Exception::class)
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain? {
        http.authorizeHttpRequests()
            .requestMatchers(HttpMethod.GET, "/API/ticket").hasRole(manager)
            .requestMatchers(HttpMethod.POST, "/API/ticket").hasRole(customer)
            .requestMatchers(HttpMethod.PUT, "/API/ticket/*/assign").hasRole(manager)
            .requestMatchers(HttpMethod.PUT, "/API/ticket/*/status").hasAnyRole(manager, expert)
            .requestMatchers(HttpMethod.GET, "/API/ticket/user/*").hasAnyRole(manager, expert)
            .requestMatchers(HttpMethod.GET, "/API/ticket/*").hasAnyRole(manager, expert)

            .requestMatchers(HttpMethod.POST, "/API/products").hasRole(customer)
            .requestMatchers(HttpMethod.GET, "/API/products").hasAnyRole(manager, expert)
            .requestMatchers(HttpMethod.GET, "/API/products/user").hasRole(customer)
            .requestMatchers(HttpMethod.GET, "/API/products/**").hasAnyRole(manager, expert)

            .requestMatchers(HttpMethod.POST, "/API/user/createExpert").hasRole(manager)
            .requestMatchers(HttpMethod.GET, "/API/users/experts").hasRole(manager)
            .requestMatchers(HttpMethod.GET, "/API/user/*").hasAnyRole(manager, expert)
            .requestMatchers(HttpMethod.PUT, "/API/user/*").hasAnyRole(manager, customer)

            .requestMatchers(HttpMethod.POST, "/API/chat").hasRole(customer)

            .requestMatchers(HttpMethod.POST, "/API/message").hasAnyRole(expert, customer)
            .requestMatchers(HttpMethod.POST, "/API/message/chat/*").hasAnyRole(manager, expert, customer)
//
            .requestMatchers(HttpMethod.POST, "/API/login").permitAll()
            .requestMatchers(HttpMethod.POST, "/API/signup").permitAll()
            .anyRequest().authenticated()

        http.oauth2ResourceServer()
            .jwt()
            .jwtAuthenticationConverter(jwtAuthConverter)
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        http.csrf().disable()
        return http.build()
    }
}