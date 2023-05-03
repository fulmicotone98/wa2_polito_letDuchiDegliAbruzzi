package wa2.polito.it.letduchidegliabruzzi.server.ticket

import wa2.polito.it.letduchidegliabruzzi.server.customer.Customer
import wa2.polito.it.letduchidegliabruzzi.server.employee.Employee
import wa2.polito.it.letduchidegliabruzzi.server.product.Product
import wa2.polito.it.letduchidegliabruzzi.server.status_history.StatusHistory

data class TicketDTO(
    val ticketID: Int? = null,
    val description: String = "",
    val status: String = " ",
    val priority: String? = null,
    val createdAt: String = "",
    var customer: Customer? = null,
    var employee: Employee? = null,
    var product: Product? = null,
    var statusHistory: List<StatusHistory>? = null
)

fun Ticket.toDTO(): TicketDTO {
    return TicketDTO(ticketID, description, status, priority, createdAt, customer, employee, product, statusHistory)
}

fun TicketDTO.toTicket(): Ticket {
    return Ticket(ticketID, description, status, priority, createdAt, customer, employee, product, statusHistory)
}