package wa2.polito.it.letduchidegliabruzzi.server.controller

import io.micrometer.observation.annotation.Observed
import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import lombok.extern.slf4j.Slf4j
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.validation.BindingResult
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import wa2.polito.it.letduchidegliabruzzi.server.controller.body.CustomerRequestBody
import wa2.polito.it.letduchidegliabruzzi.server.controller.body.CustomerResponseBody
import wa2.polito.it.letduchidegliabruzzi.server.controller.body.TicketBodyResponse
import wa2.polito.it.letduchidegliabruzzi.server.controller.httpexception.ConstraintViolationException
import wa2.polito.it.letduchidegliabruzzi.server.controller.httpexception.CustomerNotFoundException
import wa2.polito.it.letduchidegliabruzzi.server.controller.httpexception.DuplicateCustomerException
import wa2.polito.it.letduchidegliabruzzi.server.entity.customer.CustomerDTO
import wa2.polito.it.letduchidegliabruzzi.server.entity.customer.CustomerService
import wa2.polito.it.letduchidegliabruzzi.server.entity.ticket.TicketService

@Validated
@RestController
@Observed
@Slf4j
class CustomerController(private val customerService: CustomerService, private val ticketService: TicketService) {

    private val log: Logger = LoggerFactory.getLogger(ProductController::class.java)

    @GetMapping("/API/profile/{email}/tickets")
    fun getTicketsByEmail(@PathVariable("email") @Email(message = "Not an email") email: String): List<TicketBodyResponse>{
        val c = customerService.getProfile(email)
        if(c == null) {
            log.error("Get Tickets by Email error: Customer not found with Email: $email")
            throw CustomerNotFoundException("Customer not found with Email: $email")
        }
        return ticketService.getTicketsByCustomer(email).map{ TicketBodyResponse(it.ticketID,it.description,it.status,it.priority,it.createdAt, it.product.ean, it.customer.email, it.employee?.employeeID ) }
    }

    @GetMapping("/API/profiles/{email}")
    fun getProfile(@PathVariable @Email(message = "Not an email") email: String): CustomerResponseBody? {
        val c = customerService.getProfile(email)
        if(c == null) {
            log.error("Get Tickets by Email error: Customer not found with Email: $email")
            throw CustomerNotFoundException("Customer not found with Email: $email")
        }
        return CustomerResponseBody(c.email,c.name,c.surname,c.address, c.phonenumber)
    }

//    @PostMapping("/API/profiles")
//    @ResponseStatus(HttpStatus.CREATED)
//    fun addProfile(@Valid @RequestBody body: CustomerRequestBody, br: BindingResult): CustomerResponseBody? {
//        if(br.hasErrors()) {
//            log.error("Error Adding a Profile: Body validation failed with errors ${br.allErrors}")
//            throw ConstraintViolationException("Body validation failed")
//        }
//        if (customerService.getProfile(body.email) != null) {
//            log.error("Error Adding a Profile: Customer already exists with Email: ${body.email}")
//            throw DuplicateCustomerException("Customer already exists with Email: ${body.email}")
//        }
//        val customerDTO = CustomerDTO(body.name,body.surname,body.phonenumber,body.address,body.email)
//        customerService.addProfile(customerDTO)
//        log.info("Correctly added a new Profile with the email ${body.email}")
//        return CustomerResponseBody(body.email,null,null,null,null)
//    }

    @PutMapping("/API/profiles/{email}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun updateProfile(@PathVariable @Email(message = "Not an email") email: String, @Valid @RequestBody body: CustomerRequestBody, br: BindingResult): CustomerResponseBody? {
        if(br.hasErrors()) {
            log.error("Error updating a Profile: Body validation failed with errors ${br.allErrors}")
            throw ConstraintViolationException("Body validation failed")
        }
        val customerForUpdate: CustomerDTO? = customerService.getProfile(email)
        if(customerForUpdate == null){
            log.error("Error updating customer: Customer not found with Email: $email")
            throw CustomerNotFoundException("Customer not found with Email: $email")
        }
        val newCustomerDTO = CustomerDTO(body.name,body.surname,body.phonenumber, body.address, body.email)

        customerService.updateProfile(customerForUpdate, newCustomerDTO)
        log.info("Updated profile with email $email")
        return CustomerResponseBody(body.email,body.name,body.surname,body.address,body.phonenumber)
    }
}
