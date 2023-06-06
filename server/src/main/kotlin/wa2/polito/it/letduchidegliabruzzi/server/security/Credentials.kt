package wa2.polito.it.letduchidegliabruzzi.server.security

import org.keycloak.representations.idm.CredentialRepresentation

class Credentials() {

    fun createPasswordCredentials(password: String): CredentialRepresentation {
        val passwordCredentials: CredentialRepresentation = CredentialRepresentation()

        passwordCredentials.isTemporary = false
        passwordCredentials.type = CredentialRepresentation.PASSWORD
        passwordCredentials.value = password

        return passwordCredentials
    }

}