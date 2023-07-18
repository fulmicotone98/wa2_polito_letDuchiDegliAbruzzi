package wa2.polito.it.letduchidegliabruzzi.server.dal.dao.ticket

import wa2.polito.it.letduchidegliabruzzi.server.dal.dao.status_history.StatusHistoryDTO

interface TicketService {
    fun getTicket(id: Int): TicketDTO?
    fun getTickets(): List<TicketDTO>
    fun getHistory(ticket: Ticket): List<StatusHistoryDTO>
    fun addTicket(description: String, productEan: String, customerUsername: String): Ticket
    fun editTicket(newTicketDTO: TicketDTO, isStatsUpdateNeeded :Boolean = true): TicketDTO
    fun getTicketsByCustomer(customerEmail: String): List<TicketDTO>
    fun getExpertTickets(username: String): List<TicketDTO>
}