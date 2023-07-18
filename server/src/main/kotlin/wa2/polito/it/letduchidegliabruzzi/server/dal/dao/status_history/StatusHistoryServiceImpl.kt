package wa2.polito.it.letduchidegliabruzzi.server.dal.dao.status_history

import org.springframework.stereotype.Service
import wa2.polito.it.letduchidegliabruzzi.server.dal.dao.ticket.Ticket

@Service
class StatusHistoryServiceImpl(
    private val statusHistoryRepository: StatusHistoryRepository,
) : StatusHistoryService {
    override fun addStatus(ticket: Ticket, timestamp: String, status: String): StatusHistoryDTO {
        val shDTO = StatusHistoryDTO(null, ticket.ticketID!!, timestamp, status)
        return statusHistoryRepository.save(shDTO.toStatusHistory(ticket)).toDTO(ticket.ticketID)
    }

    override fun findByTicket(ticket: Ticket): List<StatusHistoryDTO> {
        return statusHistoryRepository.findByTicket(ticket).map { it.toDTO(ticket.ticketID!!) }.sortedBy { it.createdAt }
    }
}
