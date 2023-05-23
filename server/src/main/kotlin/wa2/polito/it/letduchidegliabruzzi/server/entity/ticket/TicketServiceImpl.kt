package wa2.polito.it.letduchidegliabruzzi.server.entity.ticket

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import wa2.polito.it.letduchidegliabruzzi.server.entity.customer.Customer
import wa2.polito.it.letduchidegliabruzzi.server.entity.customer.CustomerRepository
import wa2.polito.it.letduchidegliabruzzi.server.entity.product.Product
import wa2.polito.it.letduchidegliabruzzi.server.entity.product.ProductRepository
import wa2.polito.it.letduchidegliabruzzi.server.entity.status_history.StatusHistoryDTO
import wa2.polito.it.letduchidegliabruzzi.server.entity.status_history.StatusHistoryService
import java.time.LocalDate

@Service
class TicketServiceImpl(
    private val ticketRepository: TicketRepository,
    private val customerRepository: CustomerRepository,
    private val productRepository: ProductRepository,
    private val statusHistoryService: StatusHistoryService,
) : TicketService {

    @Transactional(readOnly = true, isolation = Isolation.SERIALIZABLE)
    override fun getTicket(id: Int): TicketDTO? {
        return ticketRepository.findByIdOrNull(id)?.toDTO()
    }

    @Transactional(readOnly = true, isolation = Isolation.SERIALIZABLE)
    override fun getTickets(): List<TicketDTO> {
        return ticketRepository.findAll().map { it.toDTO() }
    }

    @Transactional(readOnly = true, isolation = Isolation.SERIALIZABLE)
    override fun getHistory(ticket: Ticket): List<StatusHistoryDTO>? {
        return statusHistoryService.findByTicket(ticket).sortedBy { it.createdAt }
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    override fun addTicket(description: String, productEan: String, customerEmail: String): Ticket {
        val timestamp = LocalDate.now().toString()
        val customer= customerRepository.getReferenceById(customerEmail)
        val product = productRepository.getReferenceById(productEan)
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

    @Transactional(readOnly = true, isolation = Isolation.SERIALIZABLE)
    override fun getTicketsByCustomer(customerEmail: String): List<TicketDTO> {
        return ticketRepository.findAll().filter { it.customer.email == customerEmail}.map { it.toDTO() }
    }
}