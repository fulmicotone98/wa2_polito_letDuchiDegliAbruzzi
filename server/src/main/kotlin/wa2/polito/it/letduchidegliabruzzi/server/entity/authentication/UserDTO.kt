package wa2.polito.it.letduchidegliabruzzi.server.entity.authentication

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class UserDTO(
    @field:NotBlank val username: String,
    @field:NotBlank @field:Email val emailID: String,
    @field:NotBlank val password: String,
    @field:NotBlank val firstName: String,
    @field:NotBlank val lastName: String,
    val phoneNumber: String?,
    val address: String
)
