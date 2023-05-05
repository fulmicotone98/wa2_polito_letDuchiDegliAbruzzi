package wa2.polito.it.letduchidegliabruzzi.server.ticket

import wa2.polito.it.letduchidegliabruzzi.server.customer.Customer
import wa2.polito.it.letduchidegliabruzzi.server.product.Product

interface TicketService {
    fun getTicket(id: Int): TicketDTO?
    fun getTickets(): List<TicketDTO>
    fun addTicket(description: String, product: Product, customer: Customer): Ticket
    fun editTicket(newTicketDTO: TicketDTO): TicketDTO
}