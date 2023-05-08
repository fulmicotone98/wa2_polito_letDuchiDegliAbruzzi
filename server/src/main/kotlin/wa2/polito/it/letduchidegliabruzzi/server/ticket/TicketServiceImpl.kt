package wa2.polito.it.letduchidegliabruzzi.server.ticket

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import wa2.polito.it.letduchidegliabruzzi.server.customer.Customer
import wa2.polito.it.letduchidegliabruzzi.server.product.Product
import wa2.polito.it.letduchidegliabruzzi.server.status_history.StatusHistoryService
import java.time.LocalDate

@Service
class TicketServiceImpl(
    private val ticketRepository: TicketRepository,
    private val statusHistoryService: StatusHistoryService
) : TicketService {

    @Transactional(readOnly = true, isolation = Isolation.SERIALIZABLE)
    override fun getTicket(id: Int): TicketDTO? {
        return ticketRepository.findByIdOrNull(id)?.toDTO()
    }

    @Transactional(readOnly = true, isolation = Isolation.SERIALIZABLE)
    override fun getTickets(): List<TicketDTO> {
        return ticketRepository.findAll().map { it.toDTO() }
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    override fun addTicket(description: String, product: Product, customer: Customer): Ticket {
        val timestamp = LocalDate.now().toString()
        val newTicketDTO = TicketDTO(
            null, description, "OPEN", null,
            timestamp, customer, null, product, null
        )

        val storedTicket = ticketRepository.save(newTicketDTO.toTicket())
        statusHistoryService.addStatus(storedTicket, timestamp, "OPEN")

        return storedTicket
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    override fun editTicket(newTicketDTO: TicketDTO): TicketDTO {
        val timestamp = LocalDate.now().toString()
        statusHistoryService.addStatus(newTicketDTO.toTicket(), timestamp, newTicketDTO.status)
        return ticketRepository.save(newTicketDTO.toTicket()).toDTO()
    }
}