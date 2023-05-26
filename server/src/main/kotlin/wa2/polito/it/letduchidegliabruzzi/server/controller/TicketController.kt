package wa2.polito.it.letduchidegliabruzzi.server.controller

import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import org.springframework.http.HttpStatus
import org.springframework.validation.BindingResult
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import wa2.polito.it.letduchidegliabruzzi.server.controller.body.*
import wa2.polito.it.letduchidegliabruzzi.server.controller.httpexception.*
import wa2.polito.it.letduchidegliabruzzi.server.entity.customer.CustomerService
import wa2.polito.it.letduchidegliabruzzi.server.entity.employee.EmployeeService
import wa2.polito.it.letduchidegliabruzzi.server.entity.employee.toEmployee
import wa2.polito.it.letduchidegliabruzzi.server.entity.product.ProductService
import wa2.polito.it.letduchidegliabruzzi.server.entity.status_history.StatusHistoryService
import wa2.polito.it.letduchidegliabruzzi.server.entity.ticket.Ticket
import wa2.polito.it.letduchidegliabruzzi.server.entity.ticket.TicketService
import wa2.polito.it.letduchidegliabruzzi.server.entity.ticket.toDTO
import wa2.polito.it.letduchidegliabruzzi.server.entity.ticket.toTicket

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
    fun getTicket(@PathVariable id: Int): TicketBodyResponse? {
        val ticket = ticketService.getTicket(id)
            ?: throw TicketNotFoundException("Ticket not found with Id: $id")
        return TicketBodyResponse(ticket.ticketID, ticket.description, ticket.status, ticket.priority, ticket.createdAt, ticket.product.ean, ticket.customer.email, ticket.employee?.employeeID)
    }

    @GetMapping("/API/ticket/{id}/history")
    fun getHistory(@PathVariable id: Int): List<StatusHistoryBodyResponse> {
        val ticketDTO = ticketService.getTicket(id)
            ?: throw TicketNotFoundException("Ticket not found with Id: $id")
        return statusHistoryService.findByTicket(ticketDTO.toTicket())
            .map { StatusHistoryBodyResponse(it.statusID, it.ticket.ticketID, it.createdAt, it.status) }
    }


    @PostMapping("/API/ticket")
    @ResponseStatus(HttpStatus.CREATED)
    fun addTicket(@Valid @RequestBody body: TicketBodyRequest, br: BindingResult): TicketBodyResponse? {
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

        return TicketBodyResponse(ticket.ticketID, ticket.description, ticket.status, ticket.priority, ticket.createdAt, ticket.product.ean, ticket.customer.email, ticket.employee?.employeeID)
    }

    @PutMapping("API/ticket/{id}/assign")
    fun assignTicket(
        @PathVariable id: Int,
        @Valid @RequestBody body: AssignTicketBodyRequest,
        br: BindingResult
    ): TicketIDBodyResponse? {
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

        return TicketIDBodyResponse(ticketService.editTicket(newTicketDTO).ticketID)
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