package wa2.polito.it.letduchidegliabruzzi.server.controller

import io.micrometer.observation.annotation.Observed
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import lombok.extern.slf4j.Slf4j
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.validation.BindingResult
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import wa2.polito.it.letduchidegliabruzzi.server.controller.body.*
import wa2.polito.it.letduchidegliabruzzi.server.controller.httpexception.ConstraintViolationException
import wa2.polito.it.letduchidegliabruzzi.server.controller.httpexception.CustomerNotFoundException
import wa2.polito.it.letduchidegliabruzzi.server.controller.httpexception.DuplicateEmployeeException
import wa2.polito.it.letduchidegliabruzzi.server.controller.httpexception.EmployeeNotFoundException
import wa2.polito.it.letduchidegliabruzzi.server.dal.authDao.UserDTO
import wa2.polito.it.letduchidegliabruzzi.server.dal.authDao.UserServiceImpl
import wa2.polito.it.letduchidegliabruzzi.server.dal.authDao.addRoles
import wa2.polito.it.letduchidegliabruzzi.server.dal.dao.ticket.TicketService
import java.security.Principal

@Validated
@RestController
@Observed
@Slf4j
@RequestMapping("/API")
class UserController(private val userService: UserServiceImpl) {
    private val log: Logger = LoggerFactory.getLogger(ProductController::class.java)

    @GetMapping("/user/{username}")
    fun getProfile(@PathVariable("username") username: String): UserDTO? {
        val user = userService.getUserByUsername(username)
        if (user == null) {
            log.error("Get Tickets by Email error: Customer not found with Email: $username")
            throw CustomerNotFoundException("Customer not found with Email: $username")
        }
        return user
    }

    @PutMapping("/user")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun updateUser(principal: Principal, @Valid @RequestBody body: UserBodyNoAuth, br: BindingResult) {
        if (br.hasErrors()) {
            log.error("Error updating a Profile: Body validation failed with errors ${br.allErrors}")
            throw ConstraintViolationException("Body validation failed")
        }

        val username = principal.name
        val customerForUpdate: UserDTO? = userService.getUserByUsername(username)
        if (customerForUpdate == null) {
            log.error("Error updating customer: Customer not found with username: $username")
            throw CustomerNotFoundException("Customer not found with username: $username")
        }
        val newUserDTO = UserDTO(null, username, body.emailID, body.firstName, body.lastName, body.phoneNumber, body.address, null)

        userService.updateUserByUsername(username, newUserDTO)
        log.info("Updated profile with email $username")
    }

    @GetMapping("/userinfo")
    fun getUserInfo(auth: Authentication): UserDTO? {
        return userService.getUserByUsername(auth.name)?.addRoles(auth.authorities.map { it.authority.toString() })
    }

    @GetMapping("/users/experts")
    fun getExperts():List<UserDTO>{
        return userService.getAllExperts()
    }

    @GetMapping("/users/customers")
    fun getCustomers():List<UserDTO>{
        return userService.getAllCustomers()
    }

    @PostMapping("/user/createExpert")
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
}
