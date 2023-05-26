package wa2.polito.it.letduchidegliabruzzi.server.entity.authentication

import jakarta.validation.constraints.Email

data class UserDTO(
    val username: String,
    val emailID: String,
    val password: String,
    val firstName: String,
    val lastName: String
)
