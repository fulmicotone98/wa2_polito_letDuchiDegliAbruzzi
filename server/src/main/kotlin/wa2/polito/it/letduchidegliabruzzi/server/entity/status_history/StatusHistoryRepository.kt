package wa2.polito.it.letduchidegliabruzzi.server.entity.status_history

import org.springframework.data.jpa.repository.JpaRepository
import wa2.polito.it.letduchidegliabruzzi.server.entity.ticket.Ticket

interface StatusHistoryRepository : JpaRepository<StatusHistory, Int> {
    fun findByTicket(ticket: Ticket): List<StatusHistory>
}