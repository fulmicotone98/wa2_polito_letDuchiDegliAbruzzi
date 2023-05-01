package wa2.polito.it.letduchidegliabruzzi.server.ticket

import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

class TicketNotFoundException(message: String) : RuntimeException(message)

@Validated
@RestController
class TicketController(private val ticketService: TicketService) {

    @GetMapping("/API/ticket/{id}")
    fun getTicket(@PathVariable id: Int): TicketDTO? {
        return ticketService.getTicket(id)
            ?: throw TicketNotFoundException("Ticket not found with Id: $id")
    }
}