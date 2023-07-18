package wa2.polito.it.letduchidegliabruzzi.server.dal.dao.status_history

import org.springframework.data.jpa.repository.JpaRepository
import wa2.polito.it.letduchidegliabruzzi.server.dal.dao.ticket.Ticket

interface StatusHistoryRepository : JpaRepository<StatusHistory, Int> {
    fun findByTicket(ticket: Ticket): List<StatusHistory>
}