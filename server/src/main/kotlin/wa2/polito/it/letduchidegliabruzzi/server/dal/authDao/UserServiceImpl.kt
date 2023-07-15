package wa2.polito.it.letduchidegliabruzzi.server.dal.authDao

import org.keycloak.admin.client.resource.UsersResource
import org.keycloak.representations.idm.CredentialRepresentation
import org.keycloak.representations.idm.UserRepresentation
import org.springframework.stereotype.Service
import wa2.polito.it.letduchidegliabruzzi.server.config.KeycloakConfig
import wa2.polito.it.letduchidegliabruzzi.server.config.KeycloakProperties
import wa2.polito.it.letduchidegliabruzzi.server.controller.body.UserBody
import wa2.polito.it.letduchidegliabruzzi.server.security.Credentials
import java.util.*

@Service
class UserServiceImpl(private val keycloakProperties: KeycloakProperties):UserService {
    val instance: UsersResource = KeycloakConfig(keycloakProperties).getInstance().realm("spring_boot_webapp2_realm").users()

    override fun getUserByUsername(username: String): UserDTO? {
        val user = instance.search(username).firstOrNull()
        return user?.toDTO()

    }

    override fun addUser(userBody: UserBody, groups: List<String>): Int {
        val credentials: CredentialRepresentation = Credentials().createPasswordCredentials(userBody.password)

        val user:UserRepresentation = UserRepresentation()
        user.username = userBody.username
        user.firstName = userBody.firstName
        user.lastName = userBody.lastName
        user.email = userBody.emailID
        user.credentials = Collections.singletonList(credentials)
        user.isEnabled = true
        user.isEmailVerified = true
        user.groups = groups
        user.attributes = mapOf("address" to listOf(userBody.address) , "phonenumber" to listOf(userBody.phoneNumber))

        instance.create(user)
        println(instance.create(user).status)
        return instance.create(user).status
    }

    override fun updateUserByUsername(username: String, user: UserDTO) {
        TODO("Not yet implemented")
    }

    override fun getUserInfo(): UserDTO {
        TODO("Not yet implemented")
    }

    override fun getAllExperts(): List<UserDTO?> {
        val user = instance.search("expert").firstOrNull()
        return listOf(user?.toDTO())
    }

}
