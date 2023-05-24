package wa2.polito.it.letduchidegliabruzzi.server.security

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.transaction.Transactional
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestTemplate

@Service
@Transactional
class AuthenticationServiceImpl(): AuthenticationService {
    override fun authenticate(credentials: Credentials): String? {
        val keycloak = "http://localhost:8080/realms/SpringBootKeycloak/protocol/openid-connect/token"
        val restTemplate = RestTemplate()

        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED

        val requestBody = LinkedMultiValueMap<String, String>()
        requestBody.add("grant_type", "password")
        requestBody.add("client_id", "springboot-keycloak-client")
        requestBody.add("username", credentials.username)
        requestBody.add("password", credentials.password)

        var requestEntity = HttpEntity(requestBody, headers)

        val responseEntity = restTemplate.postForEntity(keycloak, requestEntity, KeycloakResponse::class.java)

        val keycloakResponse = responseEntity.body ?: return null

        return if (responseEntity.statusCode == HttpStatus.OK && keycloakResponse.accessToken != null) {
            keycloakResponse.accessToken
        } else {
            null
        }
    }

}
data class Credentials (
    val username: String,
    val password: String
)

data class KeycloakResponse(
    @JsonProperty("access_token")
    val accessToken: String?,
    @JsonProperty("expires_in")
    val expiresIn: Long?,
    @JsonProperty("refresh_expires_in")
    val refreshExpiresIn: Long?,
    @JsonProperty("refresh_token")
    val refreshToken: String?,
    @JsonProperty("token_type")
    val tokenType: String?,
    @JsonProperty("not_before_policy")
    val notBeforePolicy: Long?,
    @JsonProperty("session_state")
    val sessionState: String?,
    @JsonProperty("scope")
    val scope: String?
)

data class JwtResponse(
    val jwt: String
)