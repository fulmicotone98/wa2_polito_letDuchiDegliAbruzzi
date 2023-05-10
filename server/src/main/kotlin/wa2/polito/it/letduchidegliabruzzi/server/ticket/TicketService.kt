package wa2.polito.it.letduchidegliabruzzi.server.ticket

import wa2.polito.it.letduchidegliabruzzi.server.customer.Customer
import wa2.polito.it.letduchidegliabruzzi.server.product.Product
import wa2.polito.it.letduchidegliabruzzi.server.status_history.StatusHistoryDTO

interface TicketService {
    fun getTicket(id: Int): TicketDTO?
    fun getTickets(): List<TicketDTO>
    fun getHistory(ticket: Ticket): List<StatusHistoryDTO>?
    fun addTicket(description: String, productEan: String, customerEmail: String): Ticket
    fun editTicket(newTicketDTO: TicketDTO): TicketDTO
    fun getTicketsByCustomer(customerEmail: String): List<TicketDTO>
}