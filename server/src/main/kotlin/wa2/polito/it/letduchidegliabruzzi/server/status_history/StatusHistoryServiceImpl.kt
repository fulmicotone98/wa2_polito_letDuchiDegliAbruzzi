package wa2.polito.it.letduchidegliabruzzi.server.status_history

import org.springframework.stereotype.Service
import wa2.polito.it.letduchidegliabruzzi.server.ticket.Ticket

@Service
class StatusHistoryServiceImpl(private val statusHistoryRepository: StatusHistoryRepository) : StatusHistoryService {
    override fun addStatus(ticket: Ticket, timestamp: String, status: String): StatusHistoryDTO {
        val shDTO = StatusHistoryDTO(null, ticket, timestamp, status)
        return statusHistoryRepository.save(shDTO.toStatusHistory()).toDTO()
    }

    override fun getHistory(tickedID: Int): List<StatusHistoryDTO> {
        TODO("Not yet implemented")
    }
}