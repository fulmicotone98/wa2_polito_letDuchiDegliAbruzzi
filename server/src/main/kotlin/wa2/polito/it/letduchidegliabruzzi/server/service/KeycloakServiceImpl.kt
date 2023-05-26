package wa2.polito.it.letduchidegliabruzzi.server.service

import org.keycloak.admin.client.resource.UserResource
import org.keycloak.admin.client.resource.UsersResource
import org.keycloak.representations.idm.CredentialRepresentation
import org.keycloak.representations.idm.UserRepresentation
import org.springframework.stereotype.Service
import wa2.polito.it.letduchidegliabruzzi.server.config.KeycloakConfig
import wa2.polito.it.letduchidegliabruzzi.server.entity.authentication.UserDTO
import wa2.polito.it.letduchidegliabruzzi.server.security.Credentials
import java.util.Collections

@Service
class KeycloakServiceImpl(): KeycloakService {

    override fun addUser(userDTO: UserDTO) {
        val credentials: CredentialRepresentation = Credentials().createPasswordCredentials(userDTO.password)

        val user:UserRepresentation = UserRepresentation()
        user.username = userDTO.username
        user.firstName = userDTO.firstName
        user.lastName = userDTO.lastName
        user.email = userDTO.emailID
        user.credentials = Collections.singletonList(credentials)
        user.isEnabled = true

        val instance: UsersResource = getInstance()
        instance.create(user)
    }

    fun getInstance(): UsersResource {
        return KeycloakConfig().getInstance().realm(KeycloakConfig().realm).users()
    }

}