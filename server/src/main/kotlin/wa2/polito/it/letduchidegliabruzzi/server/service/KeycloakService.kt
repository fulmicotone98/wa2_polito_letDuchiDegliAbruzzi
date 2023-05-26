package wa2.polito.it.letduchidegliabruzzi.server.service

import wa2.polito.it.letduchidegliabruzzi.server.entity.authentication.UserDTO

interface KeycloakService {
    fun addUser(userDTO: UserDTO)
}