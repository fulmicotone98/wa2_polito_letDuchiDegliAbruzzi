package wa2.polito.it.letduchidegliabruzzi.server.controller

import io.micrometer.observation.annotation.Observed
import jakarta.validation.Valid
import lombok.RequiredArgsConstructor
import lombok.extern.slf4j.Slf4j
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import wa2.polito.it.letduchidegliabruzzi.server.controller.body.CredentialsLogin
import wa2.polito.it.letduchidegliabruzzi.server.controller.body.JwtResponse
import wa2.polito.it.letduchidegliabruzzi.server.controller.body.KeycloakResponse
import wa2.polito.it.letduchidegliabruzzi.server.controller.httpexception.ConstraintViolationException
import wa2.polito.it.letduchidegliabruzzi.server.controller.httpexception.DuplicateCustomerException
import wa2.polito.it.letduchidegliabruzzi.server.controller.httpexception.DuplicateEmployeeException
import wa2.polito.it.letduchidegliabruzzi.server.controller.body.UserBody
import wa2.polito.it.letduchidegliabruzzi.server.dal.authDao.UserServiceImpl
import wa2.polito.it.letduchidegliabruzzi.server.security.AuthenticationService

@RestController
@Observed
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/API")
class KeycloakController(
    private val authenticationService: AuthenticationService,
    private val userService: UserServiceImpl,) {

    private val log: Logger = LoggerFactory.getLogger(KeycloakController::class.java)
    @PostMapping("/login")
    fun login(@RequestBody credentials: CredentialsLogin): ResponseEntity<Any>{
        val keycloakResponse = authenticationService.authenticate(credentials)
        return if(keycloakResponse?.accessToken!= null){
            log.info("Created Login Token ${keycloakResponse.accessToken}")
            ResponseEntity.ok(keycloakResponse)
        } else{
            log.error("Login error: UNAUTHORIZED")
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
    }

    @PostMapping("/employee/createExpert")
    @ResponseStatus(HttpStatus.CREATED)
    fun createExpert(@Valid @RequestBody userBody: UserBody, br: BindingResult): UserBody {
        if(br.hasErrors()) {
            log.error("Error adding a new Employee: Body validation failed with error ${br.allErrors}")
            throw ConstraintViolationException("Body validation failed")
        }

        if(userService.getUserByUsername(userBody.username)!=null) {
            log.error("Error adding a new Employee: Employee already exists with Email ${userBody.emailID}")
            throw DuplicateEmployeeException("Employee already exists with Email: ${userBody.emailID}")
        }

        userService.addUser(userBody, listOf("Experts_group"))
        log.info("Correctly added a new employee with email ${userBody.emailID}")
        return userBody
    }

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    fun signup(@Valid @RequestBody userBody: UserBody, br: BindingResult): UserBody {
        if(br.hasErrors()) {
            log.error("Error Adding a Profile: Body validation failed with errors ${br.allErrors}")
            throw ConstraintViolationException("Body validation failed")
        }
        if (userService.getUserByUsername(userBody.username) != null) {
            log.error("Error Adding a Profile: Customer already exists with Username: ${userBody.username}")
            throw DuplicateCustomerException("Customer already exists with Username: ${userBody.username}")
        }
        if (userService.getUserByEmail(userBody.emailID) != null) {
            log.error("Error Adding a Profile: Customer already exists with Email: ${userBody.emailID}")
            throw DuplicateCustomerException("Customer already exists with Email: ${userBody.emailID}")
        }
        userService.addUser(userBody, listOf("Customers_group"))
        log.info("Correctly added a new Profile with the email ${userBody.emailID}")
        return userBody
    }

    @PostMapping("/logout")
    fun logout(@RequestBody oauth: KeycloakResponse): ResponseEntity<Any>{
        val httpStatus = authenticationService.logout(oauth)
        return ResponseEntity.status(httpStatus).build()
    }
}