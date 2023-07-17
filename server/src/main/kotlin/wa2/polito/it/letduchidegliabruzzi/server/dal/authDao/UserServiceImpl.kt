package wa2.polito.it.letduchidegliabruzzi.server.dal.authDao

import org.keycloak.admin.client.resource.GroupResource
import org.keycloak.admin.client.resource.GroupsResource
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
    val userResource: UsersResource = KeycloakConfig(keycloakProperties).getInstance().realm("spring_boot_webapp2_realm").users()
    val groupResource:GroupsResource = KeycloakConfig(keycloakProperties).getInstance().realm("spring_boot_webapp2_realm").groups()
    override fun getUserByUsername(username: String): UserDTO? {
        val user = userResource.search(username, true).firstOrNull()
        return user?.toDTO(null)
    }
    
    override fun getUserByEmail(email: String): UserDTO? {
        val user = userResource.search(null,null, null, email, null, null).firstOrNull()
        return user?.toDTO(null)
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

        userResource.create(user)
        println(userResource.create(user).status)
        return userResource.create(user).status
    }

    override fun updateUserByUsername(username: String, u: UserDTO): String? {
        val user = userResource.search(username, true).firstOrNull()?: return null

        val new:UserRepresentation = UserRepresentation()
        new.username = username
        new.firstName = u.name
        new.lastName = u.surname
        new.email = u.email
        new.credentials = user.credentials
        new.isEnabled = true
        new.isEmailVerified = true
        new.groups = user.groups
        new.attributes = mapOf("address" to listOf(u.address) , "phonenumber" to listOf(u.phonenumber))
        userResource.get(user.id).update(new)
        return user.id
    }

    override fun getUserInfo(): UserDTO {
        TODO("Not yet implemented")
    }

    override fun getAllExperts(): List<UserDTO> {
        //Hard-written group ID for "Experts_group"
        val experts = groupResource.group("cdf426a0-a6b6-48c3-a45a-3760436843c6").members()
        return experts.map{ it.toDTO(null) }
    }

    override fun deleteUserByUsername(username: String): String? {
        val id = getUserByUsername(username)?.id?: return null
        userResource.delete(id)
        return id
    }

}
