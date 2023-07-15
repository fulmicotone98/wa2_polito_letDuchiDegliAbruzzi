package wa2.polito.it.letduchidegliabruzzi.server.controller

import io.micrometer.observation.annotation.Observed
import jakarta.validation.Valid
import lombok.extern.slf4j.Slf4j
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.validation.BindingResult
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import wa2.polito.it.letduchidegliabruzzi.server.controller.body.CustomerRequestBody
import wa2.polito.it.letduchidegliabruzzi.server.controller.body.CustomerResponseBody
import wa2.polito.it.letduchidegliabruzzi.server.controller.body.EmployeeBodyResponse
import wa2.polito.it.letduchidegliabruzzi.server.controller.body.TicketBodyResponse
import wa2.polito.it.letduchidegliabruzzi.server.controller.httpexception.ConstraintViolationException
import wa2.polito.it.letduchidegliabruzzi.server.controller.httpexception.CustomerNotFoundException
import wa2.polito.it.letduchidegliabruzzi.server.controller.httpexception.EmployeeNotFoundException
import wa2.polito.it.letduchidegliabruzzi.server.dal.authDao.UserDTO
import wa2.polito.it.letduchidegliabruzzi.server.dal.authDao.UserServiceImpl
import wa2.polito.it.letduchidegliabruzzi.server.dal.dao.ticket.TicketService

@Validated
@RestController
@Observed
@Slf4j
class UserController(private val userService: UserServiceImpl, private val ticketService: TicketService) {

    private val log: Logger = LoggerFactory.getLogger(ProductController::class.java)

    @GetMapping("/API/profile/{username}/tickets")
    fun getTicketsByEmail(@PathVariable("username") username: String): List<TicketBodyResponse> {
        val c = userService.getUserByUsername(username)
        if (c == null) {
            log.error("Get Tickets by Email error: Customer not found with Email: $username")
            throw CustomerNotFoundException("Customer not found with Email: $username")
        }
        return ticketService.getTicketsByCustomer(username).map {
            TicketBodyResponse(
                it.ticketID,
                it.description,
                it.status,
                it.priority,
                it.createdAt,
                it.product.ean,
                it.customer.username,
                it.employee?.username
            )
        }
    }

    @GetMapping("/API/profiles/{username}")
    fun getProfile(@PathVariable("username") username: String): CustomerResponseBody? {
        val c = userService.getUserByUsername(username)
        if (c == null) {
            log.error("Get Tickets by Email error: Customer not found with Email: $username")
            throw CustomerNotFoundException("Customer not found with Email: $username")
        }
        return CustomerResponseBody(c.email, c.name, c.surname, c.address, c.phonenumber)
    }

    @GetMapping("/API/profiles/experts")
    fun getAllExperts(): List<CustomerResponseBody>? {
        val experts = userService.getAllExperts()
        return experts.filterNotNull()
            .map { c -> CustomerResponseBody(c.email, c.name, c.surname, c.address, c.phonenumber) }
    }


    @PutMapping("/API/profiles/{username}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun updateProfile(
        @PathVariable("username") username: String,
        @Valid @RequestBody body: CustomerRequestBody,
        br: BindingResult
    ): CustomerResponseBody? {
        if (br.hasErrors()) {
            log.error("Error updating a Profile: Body validation failed with errors ${br.allErrors}")
            throw ConstraintViolationException("Body validation failed")
        }
        val customerForUpdate: UserDTO? = userService.getUserByUsername(username)
        if (customerForUpdate == null) {
            log.error("Error updating customer: Customer not found with Email: $username")
            throw CustomerNotFoundException("Customer not found with Email: $username")
        }
        val newUserDTO = UserDTO(body.username, body.email, body.name, body.surname, body.phonenumber, body.address)

        userService.updateUserByUsername(username, newUserDTO)
        log.info("Updated profile with email $username")
        return CustomerResponseBody(body.email, body.name, body.surname, body.address, body.phonenumber)
    }

    @GetMapping("/employees/{username}")
    fun getEmployee(@PathVariable("username") username: String): EmployeeBodyResponse? {
        val e = userService.getUserByUsername(username)
        if (e == null) {
            log.error("Employee not found with id $username")
            throw EmployeeNotFoundException("Employee not found")
        }
        return EmployeeBodyResponse(e.username!!, e.email, e.name, "", e.surname)
    }
}
