package wa2.polito.it.letduchidegliabruzzi.server.status_history

import wa2.polito.it.letduchidegliabruzzi.server.ticket.Ticket

interface StatusHistoryService {
    fun addStatus(ticket: Ticket, timestamp: String, status: String): StatusHistoryDTO

    fun getHistory(tickedID: Int): List<StatusHistoryDTO>
}