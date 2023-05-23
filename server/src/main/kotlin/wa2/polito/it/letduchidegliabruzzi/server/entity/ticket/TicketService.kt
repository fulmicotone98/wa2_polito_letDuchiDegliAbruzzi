package wa2.polito.it.letduchidegliabruzzi.server.entity.ticket

import wa2.polito.it.letduchidegliabruzzi.server.entity.customer.Customer
import wa2.polito.it.letduchidegliabruzzi.server.entity.product.Product
import wa2.polito.it.letduchidegliabruzzi.server.entity.status_history.StatusHistoryDTO

interface TicketService {
    fun getTicket(id: Int): TicketDTO?
    fun getTickets(): List<TicketDTO>
    fun getHistory(ticket: Ticket): List<StatusHistoryDTO>?
    fun addTicket(description: String, productEan: String, customerEmail: String): Ticket
    fun editTicket(newTicketDTO: TicketDTO): TicketDTO
    fun getTicketsByCustomer(customerEmail: String): List<TicketDTO>
}