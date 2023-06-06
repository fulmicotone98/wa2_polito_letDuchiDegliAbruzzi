package wa2.polito.it.letduchidegliabruzzi.server.controller

import io.micrometer.observation.annotation.Observed
import lombok.RequiredArgsConstructor
import lombok.extern.slf4j.Slf4j
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import wa2.polito.it.letduchidegliabruzzi.server.security.AuthenticationService
import wa2.polito.it.letduchidegliabruzzi.server.security.Credentials
import wa2.polito.it.letduchidegliabruzzi.server.security.JwtResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@RestController
@Observed
@RequiredArgsConstructor
@Slf4j
class AuthenticationController(private val authenticationService: AuthenticationService) {

    private val log: Logger = LoggerFactory.getLogger(AuthenticationController::class.java)
    @PostMapping("/API/login")
    fun login(@RequestBody credentials: Credentials): ResponseEntity<Any>{
        val jwt: String? = authenticationService.authenticate(credentials)
        log.info("recieve login API")
        return if(jwt!= null){
            ResponseEntity.ok(JwtResponse(jwt))
        } else{
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
    }
}