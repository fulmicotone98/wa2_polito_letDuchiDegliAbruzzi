package wa2.polito.it.letduchidegliabruzzi.server.entity.ticket

import wa2.polito.it.letduchidegliabruzzi.server.entity.customer.Customer
import wa2.polito.it.letduchidegliabruzzi.server.entity.employee.Employee
import wa2.polito.it.letduchidegliabruzzi.server.entity.product.Product
import wa2.polito.it.letduchidegliabruzzi.server.entity.status_history.StatusHistory

data class TicketDTO(
    val ticketID: Int? = null,
    val description: String,
    val status: String,
    val priority: String? = null,
    val createdAt: String = "",
    var customer: Customer,
    var employee: Employee? = null,
    var product: Product,
    var statusHistory: List<StatusHistory>? = null
)

fun Ticket.toDTO(): TicketDTO {
    return TicketDTO(ticketID, description, status, priority, createdAt, customer, employee, product, statusHistory)
}

fun TicketDTO.toTicket(): Ticket {
    return Ticket(ticketID, description, status, priority, createdAt, customer, employee, product, statusHistory)
}