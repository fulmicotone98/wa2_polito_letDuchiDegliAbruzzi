package wa2.polito.it.letduchidegliabruzzi.server.security

import org.springframework.http.HttpStatusCode
import wa2.polito.it.letduchidegliabruzzi.server.controller.body.CredentialsLogin
import wa2.polito.it.letduchidegliabruzzi.server.controller.body.KeycloakResponse

interface AuthenticationService {
    fun authenticate(credentials: CredentialsLogin) : KeycloakResponse?
    fun logout(auth: KeycloakResponse): HttpStatusCode
}