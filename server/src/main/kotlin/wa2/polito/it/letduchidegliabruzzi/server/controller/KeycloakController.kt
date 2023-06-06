package wa2.polito.it.letduchidegliabruzzi.server.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import wa2.polito.it.letduchidegliabruzzi.server.entity.authentication.UserDTO
import wa2.polito.it.letduchidegliabruzzi.server.security.AuthenticationService
import wa2.polito.it.letduchidegliabruzzi.server.security.CredentialsLogin
import wa2.polito.it.letduchidegliabruzzi.server.security.JwtResponse
import wa2.polito.it.letduchidegliabruzzi.server.service.KeycloakService

@RestController
@RequestMapping("/API")
class KeycloakController(private val authenticationService: AuthenticationService,
    private val service: KeycloakService) {

    /*@PostMapping("/API/login")
    fun login(@RequestBody credentials: CredentialsLogin): ResponseEntity<Any>{
        val jwt: String? = authenticationService.authenticate(credentials)
        return if(jwt!= null){
            ResponseEntity.ok(JwtResponse(jwt))
        } else{
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
    }*/
    
    @PostMapping("/employee/createExpert")
    @ResponseStatus(HttpStatus.CREATED)
    fun addUser(@RequestBody userDTO: UserDTO): UserDTO {
        service.addUser(userDTO)
        return userDTO
    }


}