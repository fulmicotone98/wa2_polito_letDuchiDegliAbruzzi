package wa2.polito.it.letduchidegliabruzzi.server.status_history

import org.springframework.stereotype.Service
import wa2.polito.it.letduchidegliabruzzi.server.ticket.Ticket
import wa2.polito.it.letduchidegliabruzzi.server.ticket.TicketRepository
import java.time.LocalDate

@Service
class StatusHistoryServiceImpl(private val statusHistoryRepository: StatusHistoryRepository):StatusHistoryService  {
    override fun addStatus(ticket: Ticket, timestamp: String, status: String): StatusHistoryDTO {
        val shDTO = StatusHistoryDTO(null,ticket,timestamp, status)
        return statusHistoryRepository.save(shDTO.toStatusHistory()).toDTO()
    }

    override fun getHistory(tickedid: Int): List<StatusHistoryDTO> {
        TODO("Not yet implemented")
    }
}