package wa2.polito.it.letduchidegliabruzzi.server.config

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder
import org.keycloak.OAuth2Constants
import org.keycloak.admin.client.Keycloak
import org.keycloak.admin.client.KeycloakBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Configurable
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment

@Configuration
@Configurable
class KeycloakConfig(keycloakProperties: KeycloakProperties = KeycloakProperties()) {
    var keycloak: Keycloak? = null

//    @Autowired
//    private lateinit var environment:Environment

//    val serverUrl: String = "${environment.getProperty("keycloak.server")}"
//    val username: String = "${environment.getProperty("keycloak.admin.username")}"
//    val password: String = "${environment.getProperty("keycloak.admin.password")}"
    val serverUrl: String = keycloakProperties.serverUri
    val username: String = keycloakProperties.username
    val password: String = keycloakProperties.password
    val realm: String = "spring_boot_webapp2_realm"
    val clientID: String = "springboot-keycloak-client"
    //val clientSecret: String = "YOUR_CLIENT_SECRET_KEY"

    fun getInstance(): Keycloak {
        if (keycloak == null) {
            keycloak = KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realm)
                .grantType(OAuth2Constants.PASSWORD)
                .username(username)
                .password(password)
                .clientId(clientID)
                //.clientSecret(clientSecret)
                .resteasyClient(ResteasyClientBuilder().connectionPoolSize(10).build())
                .build()
        }

        return keycloak!!
    }

}