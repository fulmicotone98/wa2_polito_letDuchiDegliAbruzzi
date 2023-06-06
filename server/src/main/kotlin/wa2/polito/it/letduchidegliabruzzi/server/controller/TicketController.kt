package wa2.polito.it.letduchidegliabruzzi.server.controller

import io.micrometer.observation.annotation.Observed
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import lombok.extern.slf4j.Slf4j
import org.slf4j.Logger
import org.slf4j.LoggerFactory
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
@Observed
@Slf4j
class TicketController(
    private val ticketService: TicketService,
    private val statusHistoryService: StatusHistoryService,
    private val customerService: CustomerService,
    private val productService: ProductService,
    private val employeeService: EmployeeService
) {

    private val log: Logger = LoggerFactory.getLogger(ProductController::class.java)

    @GetMapping("/API/ticket/{id}")
    fun getTicket(@PathVariable id: Int): TicketBodyResponse? {
        val ticket = ticketService.getTicket(id)
        if(ticket == null){
            log.error("Error getting ticket: Ticket not found with Id $id")
            throw TicketNotFoundException("Ticket not found with Id: $id")
        }
        return TicketBodyResponse(ticket.ticketID, ticket.description, ticket.status, ticket.priority, ticket.createdAt, ticket.product.ean, ticket.customer.email, ticket.employee?.employeeID)
    }

    @GetMapping("/API/ticket/{id}/history")
    fun getHistory(@PathVariable id: Int): List<StatusHistoryBodyResponse> {
        val ticketDTO = ticketService.getTicket(id)
        if(ticketDTO == null) {
            log.error("Error getting ticket history: Ticket not found with Id $id")
            throw TicketNotFoundException("Ticket not found with Id: $id")
        }
        return statusHistoryService.findByTicket(ticketDTO.toTicket())
            .map { StatusHistoryBodyResponse(it.statusID, it.ticket.ticketID, it.createdAt, it.status) }
    }


    @PostMapping("/API/ticket")
    @ResponseStatus(HttpStatus.CREATED)
    fun addTicket(@Valid @RequestBody body: TicketBodyRequest, br: BindingResult): TicketBodyResponse? {
        if (br.hasErrors()) {
            log.error("Error adding a Ticket: Body validation failed with errors ${br.allErrors}")
            throw ConstraintViolationException("Body validation failed")
        }
        val customer =
            customerService.getProfile(body.customerEmail)
        if(customer == null){
            log.error("Error adding a Ticket: customer not found with email ${body.customerEmail}")
            throw CustomerNotFoundException("Customer not found")
        }
        val product = productService.getProduct(body.ean)
        if(product == null){
            log.error("Error adding a Ticket: product not found with ean ${body.ean}")
            throw ProductNotFoundException("Product not found")
        }
        if (product.customer.email != customer.email){
            log.error("Error adding a Ticket: No products for the customer ${customer.email}")
            throw CustomerNotFoundException("No products for the given customer")
        }

        ticketService.getTickets().forEach {
            if (it.product.ean == body.ean) {
                log.error("Error adding a Ticket: An opened ticket already exists for the ean ${body.ean}")
                throw TicketDuplicatedException("An opened ticket already exists for the ean ${body.ean}")
            }
        }
        val ticket = ticketService.addTicket(body.description, product.ean, customer.email)
        log.info("Correctly added a new ticket with id ${ticket.ticketID}")
        return TicketBodyResponse(ticket.ticketID, ticket.description, ticket.status, ticket.priority, ticket.createdAt, ticket.product.ean, ticket.customer.email, ticket.employee?.employeeID)
    }

    @PutMapping("API/ticket/{id}/assign")
    fun assignTicket(
        @PathVariable id: Int,
        @Valid @RequestBody body: AssignTicketBodyRequest,
        br: BindingResult
    ): TicketIDBodyResponse? {
        if (br.hasErrors()) {
            log.error("Error assigning a ticket: Body validation failed with errors ${br.allErrors}")
            throw ConstraintViolationException("Body validation failed")
        }
        val employee = employeeService.getEmployeeByID(body.employeeID)
        if(employee == null){
            log.error("Error Assigning the ticket $id to the employee ${body.employeeID}: Employee not found")
            throw EmployeeNotFoundException("Employee not found")
        }
        val old = ticketService.getTicket(id)
        if(old == null){
            log.error("Error Assigning the ticket $id: Ticket not found")
            throw TicketNotFoundException("Ticket not found")
        }
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
        log.info("Ticket with id ${old.ticketID} correctly assigned to ${employee.employeeID}")
        return TicketIDBodyResponse(ticketService.editTicket(newTicketDTO).ticketID)
    }

    @PutMapping("API/ticket/{id}/status")
    fun editTicketStatus(@PathVariable id: Int, @Valid @RequestBody body: BodyStatusTicket, br: BindingResult): Int? {
        if (br.hasErrors()) {
            log.error("Error editing the ticket status: Body validation failed with error ${br.allErrors}")
            throw ConstraintViolationException("Body validation failed")
        }
        val old = ticketService.getTicket(id)
        if(old == null){
            log.error("Error editing the ticket status: Ticket not found with id $id")
            throw TicketNotFoundException("Ticket not found")
        }
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
        log.info("Ticket $id status correctly edited to from ${old.status} to ${body.status}")
        return ticketService.editTicket(newTicketDTO).toTicket().ticketID
    }
}