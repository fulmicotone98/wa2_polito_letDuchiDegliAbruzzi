package wa2.polito.it.letduchidegliabruzzi.server.dal.dao.ticket

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import wa2.polito.it.letduchidegliabruzzi.server.dal.authDao.UserServiceImpl
import wa2.polito.it.letduchidegliabruzzi.server.dal.dao.chat.toDTO
import wa2.polito.it.letduchidegliabruzzi.server.dal.dao.product.ProductRepository
import wa2.polito.it.letduchidegliabruzzi.server.dal.dao.product.toDTO
import wa2.polito.it.letduchidegliabruzzi.server.dal.dao.status_history.StatusHistoryDTO
import wa2.polito.it.letduchidegliabruzzi.server.dal.dao.status_history.StatusHistoryService
import java.time.Instant
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
class TicketServiceImpl(
    private val userService: UserServiceImpl,
    private val ticketRepository: TicketRepository,
    private val productRepository: ProductRepository,
    private val statusHistoryService: StatusHistoryService,
) : TicketService {

    @Transactional(readOnly = true, isolation = Isolation.SERIALIZABLE)
    override fun getTicket(id: Int): TicketDTO? {
        val ticket = ticketRepository.findByIdOrNull(id)?: return null
        val customer = userService.getUserByUsername(ticket.customerUsername) ?: return null
        val expert = userService.getUserByUsername(ticket.expertUsername?:"")
        val ticketHistory = statusHistoryService.findByTicket(ticket)
        return ticket.toDTO(customer,expert,ticketHistory,ticket.chat?.toDTO())
    }

    @Transactional(readOnly = true, isolation = Isolation.SERIALIZABLE)
    override fun getTickets(): List<TicketDTO> {
        val tickets = ticketRepository.findAll()
            .filter{ userService.getUserByUsername(it.customerUsername)!=null }
            .map{
                val customer = userService.getUserByUsername(it.customerUsername)
                val expert = userService.getUserByUsername(it.expertUsername?:"")
                val ticketHistory = statusHistoryService.findByTicket(it)
                it.toDTO(customer!!, expert,ticketHistory,it.chat?.toDTO())
            }
        return tickets
    }

    @Transactional(readOnly = true, isolation = Isolation.SERIALIZABLE)
    override fun getHistory(ticket: Ticket): List<StatusHistoryDTO> {
        return statusHistoryService.findByTicket(ticket).sortedBy { it.createdAt }
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    override fun addTicket(description: String, productEan: String, customerUsername: String): Ticket {
        val timestamp = DateTimeFormatter.ISO_INSTANT.format(Instant.now()).toString()
        val customer = userService.getUserByUsername(customerUsername)
        val product = productRepository.getReferenceById(productEan)
        val newTicketDTO = TicketDTO(
            null, description, "OPEN", null,
            timestamp, customer!!, null, product.toDTO(customer), emptyList<StatusHistoryDTO>(),null)

        val storedTicket = ticketRepository.save(newTicketDTO.toTicket())
        statusHistoryService.addStatus(storedTicket, timestamp, "OPEN")

        return storedTicket
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    override fun editTicket(newTicketDTO: TicketDTO, isStatsUpdateNeeded :Boolean ): TicketDTO {
        val timestamp = DateTimeFormatter.ISO_INSTANT.format(Instant.now()).toString()
        if(isStatsUpdateNeeded) {
            statusHistoryService.addStatus(newTicketDTO.toTicket(), timestamp, newTicketDTO.status)
        }
        val modified = ticketRepository.save(newTicketDTO.toTicket())
        val customer = userService.getUserByUsername(modified.customerUsername)
        val expert = userService.getUserByUsername(modified.expertUsername?:"")
        val ticketHistory = statusHistoryService.findByTicket(newTicketDTO.toTicket())
        return modified.toDTO(customer!!,expert,ticketHistory,modified.chat?.toDTO())
    }
    @Transactional(readOnly = true, isolation = Isolation.SERIALIZABLE)
    override fun getTicketsByCustomer(customerUsername: String): List<TicketDTO> {
        return ticketRepository.findAll().filter { it.customerUsername == customerUsername}.map{
            val customer = userService.getUserByUsername(it.customerUsername)
            val expert = userService.getUserByUsername(it.expertUsername?:"")
            val ticketHistory = statusHistoryService.findByTicket(it)
            it.toDTO(customer!!, expert,ticketHistory,it.chat?.toDTO())
        }
    }

    override fun getExpertTickets(username: String): List<TicketDTO> {
        val expert = userService.getUserByUsername(username)
        return ticketRepository.findAll().filter { it.expertUsername == username}.map{
            val customer = userService.getUserByUsername(it.customerUsername)
            val ticketHistory = statusHistoryService.findByTicket(it)
            it.toDTO(customer!!, expert,ticketHistory,it.chat?.toDTO())
        }
    }
}