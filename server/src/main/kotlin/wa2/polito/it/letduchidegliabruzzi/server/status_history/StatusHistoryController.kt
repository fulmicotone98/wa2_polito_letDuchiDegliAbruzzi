package wa2.polito.it.letduchidegliabruzzi.server.status_history

import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import wa2.polito.it.letduchidegliabruzzi.server.ticket.TicketNotFoundException
import wa2.polito.it.letduchidegliabruzzi.server.ticket.TicketService
import wa2.polito.it.letduchidegliabruzzi.server.ticket.toTicket

@Validated
@RestController
class StatusHistoryController(
    private val statusHistoryService: StatusHistoryService,
    private val ticketService: TicketService
) {

    @GetMapping("API/ticket/{id}/status_history")
    fun getHistory(@PathVariable id: Int): BodyStatusHistoryList {
        val ticketDTO = ticketService.getTicket(id)
            ?: throw TicketNotFoundException("Ticket not found with Id: $id")
        return BodyStatusHistoryList(statusHistoryService.getHistory(ticketDTO.toTicket()))
    }
}

data class BodyStatusHistoryList(
    val statusHistoryList: List<StatusHistoryDTO>?
)