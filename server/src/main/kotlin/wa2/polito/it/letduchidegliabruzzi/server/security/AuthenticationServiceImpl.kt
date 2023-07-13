package wa2.polito.it.letduchidegliabruzzi.server.security

import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestTemplate
import wa2.polito.it.letduchidegliabruzzi.server.controller.body.CredentialsLogin
import wa2.polito.it.letduchidegliabruzzi.server.controller.body.KeycloakResponse

@Service
@Transactional
class AuthenticationServiceImpl(): AuthenticationService {
    @Autowired
    private lateinit var environment: Environment
    override fun authenticate(credentials: CredentialsLogin): KeycloakResponse? {

        val keycloak = "${environment.getProperty("spring.security.oauth2.resourceserver.jwt.issuer-uri")}/protocol/openid-connect/token"
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
            // keycloakResponse.accessToken
            keycloakResponse
        } else {
            null
        }
    }

    override fun logout(auth: KeycloakResponse): HttpStatusCode{
        val keycloak = "${environment.getProperty("spring.security.oauth2.resourceserver.jwt.issuer-uri")}/protocol/openid-connect/logout"
        val restTemplate = RestTemplate()

        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED
        headers.set("Authorization", "Bearer "+auth.accessToken)

        val requestBody = LinkedMultiValueMap<String, String>()
        requestBody.add("client_id", "springboot-keycloak-client")
        requestBody.add("refresh_token", auth.refreshToken)

        val requestEntity = HttpEntity(requestBody, headers)

        val responseEntity = restTemplate.postForEntity(keycloak, requestEntity,Unit::class.java)

        return responseEntity.statusCode
    }

}



