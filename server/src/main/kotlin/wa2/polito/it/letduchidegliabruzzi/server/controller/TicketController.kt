package wa2.polito.it.letduchidegliabruzzi.server.controller

import io.micrometer.observation.annotation.Observed
import jakarta.validation.Valid
import lombok.extern.slf4j.Slf4j
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.validation.BindingResult
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import wa2.polito.it.letduchidegliabruzzi.server.controller.body.*
import wa2.polito.it.letduchidegliabruzzi.server.controller.httpexception.*
import wa2.polito.it.letduchidegliabruzzi.server.dal.authDao.UserServiceImpl
import wa2.polito.it.letduchidegliabruzzi.server.dal.dao.chat.toChat
import wa2.polito.it.letduchidegliabruzzi.server.dal.dao.product.ProductService
import wa2.polito.it.letduchidegliabruzzi.server.dal.dao.product.toProduct
import wa2.polito.it.letduchidegliabruzzi.server.dal.dao.status_history.StatusHistoryService
import wa2.polito.it.letduchidegliabruzzi.server.dal.dao.status_history.toStatusHistory
import wa2.polito.it.letduchidegliabruzzi.server.dal.dao.ticket.*
import java.security.Principal

@Validated
@RestController
@Observed
@Slf4j
@RequestMapping("/API")
class TicketController(
    private val ticketService: TicketService,
    private val statusHistoryService: StatusHistoryService,
    private val productService: ProductService,
    private val userService: UserServiceImpl
) {

    private val log: Logger = LoggerFactory.getLogger(ProductController::class.java)

//    TODO(GET user tickets)
//    TODO(GET expert tickets)

    @GetMapping("/ticket/{id}")
    fun getTicket(@PathVariable id: Int): TicketDTO? {
        val ticket = ticketService.getTicket(id)
        if(ticket == null){
            log.error("Error getting ticket: Ticket not found with Id $id")
            throw TicketNotFoundException("Ticket not found with Id: $id")
        }
        return ticket
    }

    @GetMapping("/API/ticket")
    fun getTickets(): List<TicketDTO>? {
        return ticketService.getTickets()
    }

    @PostMapping("/ticket")
    @ResponseStatus(HttpStatus.CREATED)
    fun addTicket(@Valid @RequestBody body: TicketBodyRequest, br: BindingResult, principal: Principal): TicketBodyResponse? {
        // Check if the body is valid
        if (br.hasErrors()) {
            log.error("Error adding a Ticket: Body validation failed with errors ${br.allErrors}")
            throw ConstraintViolationException("Body validation failed")
        }

        val customerUsername = principal.name

        // Check if the product exists
        val product = productService.getProduct(body.ean)
        if(product == null){
            log.error("Error adding a Ticket: product not found with ean ${body.ean}")
            throw ProductNotFoundException("Product not found")
        }

        // Check if the customer owns that product
        if (product.customer.username != customerUsername){
            log.error("Error adding a Ticket: No products for the customer $customerUsername")
            throw ProductNotFoundException("Product not found")
        }

        // Check if the ticket has been already opened or if it is not close
        val ok = ticketService.getTickets().all { it.product.ean != product.ean || it.status == "CLOSED"}
        if (!ok) {
            log.error("Error adding a Ticket: An opened ticket already exists for the ean ${body.ean}")
            throw TicketDuplicatedException("An opened ticket already exists for the ean ${body.ean}")
        }

        val ticket = ticketService.addTicket(body.description, product.ean, customerUsername)
        log.info("Correctly added a new ticket with id ${ticket.ticketID}")
        return TicketBodyResponse(ticket.ticketID, ticket.description, ticket.status, ticket.priority, ticket.createdAt, ticket.product.ean, ticket.customerUsername, ticket.expertUsername)
    }

    @PutMapping("/ticket/{id}/assign")
    fun assignTicket(@PathVariable id: Int, @Valid @RequestBody body: AssignTicketBodyRequest, br: BindingResult): TicketIDBodyResponse? {
        // Check if the body is valid
        if (br.hasErrors()) {
            log.error("Error assigning a ticket: Body validation failed with errors ${br.allErrors}")
            throw ConstraintViolationException("Body validation failed")
        }

        // Check if the employee exists
        val employee = userService.getUserByUsername(body.employeeUsername)
        if(employee == null){
            log.error("Error Assigning the ticket $id to the employee ${body.employeeUsername}: Employee not found")
            throw EmployeeNotFoundException("Employee not found")
        }

        // Check if the ticket exists
        val ticket = ticketService.getTicket(id)
        if(ticket == null){
            log.error("Error Assigning the ticket $id: Ticket not found")
            throw TicketNotFoundException("Ticket not found")
        }

        val ticketHistory = statusHistoryService.findByTicket(ticket.toTicket())

        val newTicketDTO = Ticket(
            ticket.ticketID,
            ticket.description,
            "IN PROGRESS",
            body.priority,
            ticket.createdAt,
            ticket.customer.username,
            employee.username,
            ticket.product.toProduct(),
            null
        ).toDTO(ticket.customer, employee,ticketHistory,null)
        log.info("Ticket with id ${ticket.ticketID} correctly assigned to ${employee.username}")
        return TicketIDBodyResponse(ticketService.editTicket(newTicketDTO).ticketID)
    }

    @PutMapping("/ticket/{id}/status")
    fun editTicketStatus(@PathVariable id: Int, @Valid @RequestBody body: BodyStatusTicket, br: BindingResult): Int? {
        if (br.hasErrors()) {
            log.error("Error editing the ticket status: Body validation failed with error ${br.allErrors}")
            throw ConstraintViolationException("Body validation failed")
        }
        val ticket = ticketService.getTicket(id)
        if(ticket == null){
            log.error("Error editing the ticket status: Ticket not found with id $id")
            throw TicketNotFoundException("Ticket not found")
        }
        val ticketHistory = statusHistoryService.findByTicket(ticket.toTicket())
        val newTicketDTO = Ticket(
            ticket.ticketID,
            ticket.description,
            body.status,
            ticket.priority,
            ticket.createdAt,
            ticket.customer.username,
            ticket.employee?.username,
            ticket.product.toProduct(),
            ticket.chat?.toChat()
        ).toDTO(ticket.customer, ticket.employee,ticketHistory,ticket.chat)
        log.info("Ticket $id status correctly edited to from ${ticket.status} to ${body.status}")
        return ticketService.editTicket(newTicketDTO).toTicket().ticketID
    }

    @GetMapping("/ticket/user/{username}")
    fun getTicketsByEmail(@PathVariable("username") username: String): List<TicketDTO> {
        val c = userService.getUserByUsername(username)
        if (c == null) {
            log.error("Get Tickets by Email error: Customer not found with Email: $username")
            throw CustomerNotFoundException("Customer not found with Email: $username")
        }
        return ticketService.getTicketsByCustomer(username)
    }
}