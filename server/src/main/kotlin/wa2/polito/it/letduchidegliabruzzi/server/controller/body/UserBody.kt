package wa2.polito.it.letduchidegliabruzzi.server.controller.body

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class UserBody(
    @field:NotBlank val username: String,
    @field:NotBlank @field:Email val emailID: String,
    @field:NotBlank val password: String,
    @field:NotBlank val firstName: String,
    @field:NotBlank val lastName: String,
    val phoneNumber: String?,
    val address: String
)
