package wa2.polito.it.letduchidegliabruzzi.server.ticket

import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
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
import wa2.polito.it.letduchidegliabruzzi.server.employee.EmployeeNotFoundException
import wa2.polito.it.letduchidegliabruzzi.server.employee.EmployeeService
import wa2.polito.it.letduchidegliabruzzi.server.employee.toEmployee
import wa2.polito.it.letduchidegliabruzzi.server.product.ProductNotFoundException
import wa2.polito.it.letduchidegliabruzzi.server.product.ProductService
import wa2.polito.it.letduchidegliabruzzi.server.status_history.StatusHistoryService

class TicketNotFoundException(message: String) : RuntimeException(message)
class TicketDuplicatedException(message: String) : RuntimeException(message)
class ConstraintViolationException(message: String) : RuntimeException(message)


@Validated
@RestController
class TicketController(
    private val ticketService: TicketService,
    private val statusHistoryService: StatusHistoryService,
    private val customerService: CustomerService,
    private val productService: ProductService,
    private val employeeService: EmployeeService
) {

    @GetMapping("/API/ticket/{id}")
    fun getTicket(@PathVariable id: Int): TicketResponseBody? {
        val ticket = ticketService.getTicket(id)
            ?: throw TicketNotFoundException("Ticket not found with Id: $id")
        return TicketResponseBody(ticket.ticketID, ticket.description, ticket.status, ticket.priority, ticket.createdAt, ticket.product.ean, ticket.customer.email, ticket.employee?.employeeID)
    }

    @GetMapping("/API/ticket/{id}/history")
    fun getHistory(@PathVariable id: Int): List<BodyStatusHistoryList> {
        val ticketDTO = ticketService.getTicket(id)
            ?: throw TicketNotFoundException("Ticket not found with Id: $id")
        return statusHistoryService.findByTicket(ticketDTO.toTicket())
            .map { BodyStatusHistoryList(it.statusID, it.ticket.ticketID, it.createdAt, it.status) }
    }


    @PostMapping("/API/ticket")
    fun addTicket(@Valid @RequestBody body: BodyObject, br: BindingResult): TicketResponseBody? {
        if (br.hasErrors())
            throw ConstraintViolationException("Body validation failed")

        val customer =
            customerService.getProfile(body.customerEmail) ?: throw CustomerNotFoundException("Customer not found")
        val product = productService.getProduct(body.ean) ?: throw ProductNotFoundException("Product not found")
        if (product.customer.email != customer.email)
            throw CustomerNotFoundException("No products for the given customer")
        ticketService.getTickets().forEach {
            if (it.product.ean == body.ean)
                throw TicketDuplicatedException("An opened ticket already exists for the ean ${body.ean}")
        }
        val ticket = ticketService.addTicket(body.description, product.ean, customer.email)

        return TicketResponseBody(ticket.ticketID, ticket.description, ticket.status, ticket.priority, ticket.createdAt, ticket.product.ean, ticket.customer.email, ticket.employee?.employeeID)
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

data class BodyStatusHistoryList(
    @field:NotBlank val statusID: Int?,
    @field:NotBlank val ticketID: Int?,
    @field:NotBlank val createdAt: String,
    @field:NotBlank val status: String
)

data class TicketResponseBody(
    @field:Positive val ticketID: Int?,
    @field:NotBlank val description: String,
    @field:NotBlank val status: String,
    @field:NotBlank val priority: String?,
    @field:NotBlank val createdAt: String,
    @field:NotBlank val productEan: String,
    @field:NotBlank val customerEmail: String,
    val employeeId: Int?
)

data class BodyObject(
    @field:NotNull @field:NotBlank val ean: String,
    @field:NotNull @field:NotBlank val description: String,
    @field:NotNull @field:NotBlank val customerEmail: String
)

data class BodyAssignTicketObject(
    @field:NotNull @field:Positive val employeeID: Int,
    @field:NotNull @field:NotBlank val priority: String
)

data class BodyResponse(val ticketID: Int?)

data class BodyStatusTicket(@field:NotBlank val status: String)