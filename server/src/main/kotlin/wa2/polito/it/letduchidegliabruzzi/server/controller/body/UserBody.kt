package wa2.polito.it.letduchidegliabruzzi.server.controller.body

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class UserBody(
    @field:NotBlank val username: String,
    @field:NotBlank @field:Email val emailID: String,
    @field:NotBlank val password: String,
    @field:NotBlank val firstName: String,
    @field:NotBlank val lastName: String,
    @field:NotBlank val phoneNumber: String,
    @field:NotBlank val address: String
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

data class CredentialsLogin (
    val username: String,
    val password: String
)

data class JwtResponse(
    val jwt: String
)
