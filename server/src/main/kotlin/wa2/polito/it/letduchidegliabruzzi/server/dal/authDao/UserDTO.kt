package wa2.polito.it.letduchidegliabruzzi.server.dal.authDao

import org.keycloak.representations.idm.UserRepresentation

data class UserDTO(
    val username: String,
    val email: String,
    val name: String,
    val surname: String,
    val phonenumber: String?,
    val address: String?
)

fun UserRepresentation.toDTO(): UserDTO{
    return UserDTO(username,email,firstName,lastName, attributes["phonenumber"]?.firstOrNull(),
        attributes["address"]?.firstOrNull())
}