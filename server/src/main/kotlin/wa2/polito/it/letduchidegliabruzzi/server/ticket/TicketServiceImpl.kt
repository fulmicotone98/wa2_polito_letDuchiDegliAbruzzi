package wa2.polito.it.letduchidegliabruzzi.server.ticket

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class TicketServiceImpl(private val ticketRepository: TicketRepository): TicketService {

    override fun getTicket(id: Int): TicketDTO? {
        return ticketRepository.findByIdOrNull(id)?.toDTO()
    }
}