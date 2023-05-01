package wa2.polito.it.letduchidegliabruzzi.server.ticket

interface TicketService {
    fun getTicket(id: Int): TicketDTO?
}