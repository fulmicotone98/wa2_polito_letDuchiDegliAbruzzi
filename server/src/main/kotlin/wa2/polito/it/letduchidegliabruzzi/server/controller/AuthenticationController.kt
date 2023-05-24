package wa2.polito.it.letduchidegliabruzzi.server.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import wa2.polito.it.letduchidegliabruzzi.server.security.AuthenticationService
import wa2.polito.it.letduchidegliabruzzi.server.security.Credentials
import wa2.polito.it.letduchidegliabruzzi.server.security.JwtResponse

@RestController
class AuthenticationController(private val authenticationService: AuthenticationService) {
    fun login(@RequestBody credentials: Credentials): ResponseEntity<Any>{
        val jwt: String? = authenticationService.authenticate(credentials)
        return if(jwt!= null){
            ResponseEntity.ok(JwtResponse(jwt))
        } else{
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
    }
}