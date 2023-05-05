package wa2.polito.it.letduchidegliabruzzi.server.ticket

import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import org.springframework.validation.BindingResult
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import wa2.polito.it.letduchidegliabruzzi.server.customer.CustomerNotFoundException
import wa2.polito.it.letduchidegliabruzzi.server.customer.CustomerService
import wa2.polito.it.letduchidegliabruzzi.server.customer.toCustomer
import wa2.polito.it.letduchidegliabruzzi.server.employee.EmployeeNotFoundException
import wa2.polito.it.letduchidegliabruzzi.server.employee.EmployeeService
import wa2.polito.it.letduchidegliabruzzi.server.employee.toEmployee
import wa2.polito.it.letduchidegliabruzzi.server.product.ProductNotFoundException
import wa2.polito.it.letduchidegliabruzzi.server.product.ProductService
import wa2.polito.it.letduchidegliabruzzi.server.product.toProduct

class TicketNotFoundException(message: String) : RuntimeException(message)
class TicketDuplicatedException(message: String) : RuntimeException(message)
class ConstraintViolationException(message: String) : RuntimeException(message)


@Validated
@RestController
class TicketController(
    private val ticketService: TicketService,
    private val customerService: CustomerService,
    private val productService: ProductService,
    private val employeeService: EmployeeService
) {

    @GetMapping("/API/ticket/{id}")
    fun getTicket(@PathVariable id: Int): TicketDTO? {
        return ticketService.getTicket(id)
            ?: throw TicketNotFoundException("Ticket not found with Id: $id")
    }

    @PostMapping("/API/ticket")
    fun addTicket(@Valid @RequestBody body: BodyObject, br: BindingResult): TicketDTO? {
        if (br.hasErrors())
            throw ConstraintViolationException("Body validation failed")

        val customer =
            customerService.getProfile(body.customerEmail) ?: throw CustomerNotFoundException("Customer not found")
        val product = productService.getProduct(body.ean) ?: throw ProductNotFoundException("Product not found")
        if (product.customer?.email != customer.email)
            throw CustomerNotFoundException("No products for the given customer")
        ticketService.getTickets().forEach {
            if (it.product?.ean == body.ean)
                throw TicketDuplicatedException("An opened ticket already exists for the ean ${body.ean}")
        }
        return ticketService.addTicket(body.description, product.toProduct(), customer.toCustomer()).toDTO()
    }

    @PutMapping("API/ticket/{id}/assign")
    fun assignTicket(
        @PathVariable id: Int,
        @Valid @RequestBody body: BodyAssignTicketObject,
        br: BindingResult
    ): BodyResponse? {
        if (br.hasErrors())
            throw ConstraintViolationException("Body validation failed")
        val employee =
            employeeService.getEmployeeByID(body.employeeID) ?: throw EmployeeNotFoundException("Employee not found")
        val old = ticketService.getTicket(id) ?: throw TicketNotFoundException("Ticket not found")
        val newTicketDTO = Ticket(
            old.ticketID,
            old.description,
            "IN PROGRESS",
            body.priority,
            old.createdAt,
            old.customer,
            employee.toEmployee(),
            old.product,
            old.statusHistory
        ).toDTO()

        return BodyResponse(ticketService.editTicket(newTicketDTO).ticketID)
    }

    @PutMapping("API/ticket/{id}/status")
    fun editTicketStatus(@PathVariable id: Int, @Valid @RequestBody body: BodyStatusTicket, br: BindingResult): Int? {
        if (br.hasErrors())
            throw ConstraintViolationException("Body validation failed")
        val old = ticketService.getTicket(id) ?: throw TicketNotFoundException("Ticket not found")
        val newTicketDTO = Ticket(
            old.ticketID,
            old.description,
            body.status,
            old.priority,
            old.createdAt,
            old.customer,
            old.employee,
            old.product,
            old.statusHistory
        ).toDTO()

        return ticketService.editTicket(newTicketDTO).toTicket().ticketID
    }
}

data class BodyObject(
    @field:NotBlank val ean: String,
    @field:NotBlank val description: String,
    @field:NotBlank val customerEmail: String
)

data class BodyAssignTicketObject(
    @field:Positive val employeeID: Int,
    @field:NotBlank val priority: String
)

data class BodyResponse(val id: Int?)

data class BodyStatusTicket(@field:NotBlank val status: String)