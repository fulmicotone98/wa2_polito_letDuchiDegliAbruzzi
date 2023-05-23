package wa2.polito.it.letduchidegliabruzzi.server.entity.status_history

import org.springframework.stereotype.Service
import wa2.polito.it.letduchidegliabruzzi.server.entity.ticket.Ticket

@Service
class StatusHistoryServiceImpl(
    private val statusHistoryRepository: StatusHistoryRepository,
) : StatusHistoryService {
    override fun addStatus(ticket: Ticket, timestamp: String, status: String): StatusHistoryDTO {
        val shDTO = StatusHistoryDTO(null, ticket, timestamp, status)
        return statusHistoryRepository.save(shDTO.toStatusHistory()).toDTO()
    }

    override fun findByTicket(ticket: Ticket): List<StatusHistoryDTO> {
        return statusHistoryRepository.findByTicket(ticket).map { it.toDTO() }.sortedBy { it.createdAt }
    }
}
