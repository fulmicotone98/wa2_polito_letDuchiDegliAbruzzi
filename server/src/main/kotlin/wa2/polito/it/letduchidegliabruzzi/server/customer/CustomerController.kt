package wa2.polito.it.letduchidegliabruzzi.server.customer

import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
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
import wa2.polito.it.letduchidegliabruzzi.server.ticket.ConstraintViolationException
import wa2.polito.it.letduchidegliabruzzi.server.ticket.Ticket
import wa2.polito.it.letduchidegliabruzzi.server.ticket.TicketResponseBody
import wa2.polito.it.letduchidegliabruzzi.server.ticket.TicketService

class CustomerNotFoundException(message: String) : RuntimeException(message)
class DuplicateCustomerException(message: String) : RuntimeException(message)

@Validated
@RestController
class CustomerController(private val customerService: CustomerService, private val ticketService: TicketService) {

    @GetMapping("/API/profile/{email}/tickets")
    fun getTicketsByEmail(@PathVariable("email") @Email(message = "Not an email") email: String): List<TicketResponseBody>{
        val c = customerService.getProfile(email)
            ?: throw CustomerNotFoundException("Customer not found with Email: $email")
        return ticketService.getTicketsByCustomer(email).map{ TicketResponseBody(it.ticketID,it.description,it.status,it.priority,it.createdAt) }
    }

    @GetMapping("/API/profiles/{email}")
    fun getProfile(@PathVariable @Email(message = "Not an email") email: String): CustomerResponseBody? {
        val c = customerService.getProfile(email)
            ?: throw CustomerNotFoundException("Customer not found with Email: $email")

        return CustomerResponseBody(c.email,c.name,c.surname,c.address, c.phonenumber)
    }

    @PostMapping("/API/profiles")
    @ResponseStatus(HttpStatus.CREATED)
    fun addProfile(@Valid @RequestBody body: CustomerRequestBody, br: BindingResult): CustomerResponseBody? {
        if(br.hasErrors())
            throw ConstraintViolationException("Body validation failed")
        if (customerService.getProfile(body.email) != null)
            throw DuplicateCustomerException("Customer already exists with Email: ${body.email}")

        val customerDTO = CustomerDTO(body.name,body.surname,body.phonenumber,body.address,body.email)
        customerService.addProfile(customerDTO)
        return CustomerResponseBody(body.email,null,null,null,null)
    }

    @PutMapping("/API/profiles/{email}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun updateProfile(@PathVariable @Email(message = "Not an email") email: String, @Valid @RequestBody body: CustomerRequestBody,br: BindingResult): CustomerResponseBody? {
        if(br.hasErrors())
            throw ConstraintViolationException("Body validation failed")

        val customerForUpdate: CustomerDTO = customerService.getProfile(email)
            ?:throw CustomerNotFoundException("Customer not found with Email: $email")
        val newCustomerDTO = CustomerDTO(body.name,body.surname,body.phonenumber, body.address, body.email)

        customerService.updateProfile(customerForUpdate, newCustomerDTO)
        return CustomerResponseBody(body.email,body.name,body.surname,body.address,body.phonenumber)
    }
}

data class CustomerResponseBody(
    @field:NotNull @field:NotBlank @field:Email val email: String?,
    @field:NotNull @field:NotBlank val name: String?,
    @field:NotNull @field:NotBlank val surname: String?,
    @field:NotNull @field:NotBlank val address: String?,
    val phonenumber: String?
)
data class CustomerRequestBody(
    @field:NotNull @field:NotBlank @field:Email(message = "The email should be provided in a correct format") val email: String = "",
    @field:NotNull @field:NotBlank(message = "The name should not be blank") val name: String= "",
    @field:NotNull @field:NotBlank(message = "The surname should not be blank") val surname: String = "",
    @field:NotNull @field:NotBlank(message = "The address should not be blank") val address: String = "",
    val phonenumber: String = ""
)