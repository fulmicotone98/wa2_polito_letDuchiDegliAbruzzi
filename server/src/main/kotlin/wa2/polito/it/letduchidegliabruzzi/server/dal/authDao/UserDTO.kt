package wa2.polito.it.letduchidegliabruzzi.server.dal.authDao

import org.keycloak.representations.idm.UserRepresentation

data class UserDTO(
    val id: String?,
    val username: String,
    val email: String,
    val name: String,
    val surname: String,
    val phonenumber: String?,
    val address: String?,
    val roles: List<String>?
)

fun UserRepresentation.toDTO(roles: List<String>?): UserDTO{
    return UserDTO(id,username,email,firstName,lastName, attributes["phonenumber"]?.firstOrNull(),
        attributes["address"]?.firstOrNull(), roles)
}

fun UserDTO.addRoles(roles: List<String>): UserDTO{
    return UserDTO(id,username,email,name,surname,phonenumber, address, roles)
}