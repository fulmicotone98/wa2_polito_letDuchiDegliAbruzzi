package wa2.polito.it.letduchidegliabruzzi.server.ticket

interface TicketService {
    fun getTicket(id: Int): TicketDTO?

    fun addTicket(description: String, ean: String, customerEmail: String): Ticket
}