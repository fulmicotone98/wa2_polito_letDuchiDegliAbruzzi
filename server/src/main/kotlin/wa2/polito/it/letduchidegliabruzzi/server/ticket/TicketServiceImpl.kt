package wa2.polito.it.letduchidegliabruzzi.server.ticket

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import wa2.polito.it.letduchidegliabruzzi.server.customer.Customer
import wa2.polito.it.letduchidegliabruzzi.server.customer.CustomerRepository
import wa2.polito.it.letduchidegliabruzzi.server.product.Product
import wa2.polito.it.letduchidegliabruzzi.server.product.ProductRepository
import java.time.LocalDate

@Service
class TicketServiceImpl(private val ticketRepository: TicketRepository,
    private val customerRepository: CustomerRepository,
    private val productRepository: ProductRepository): TicketService {

    override fun getTicket(id: Int): TicketDTO? {
        return ticketRepository.findByIdOrNull(id)?.toDTO()
    }

    override fun addTicket(description: String, ean: String, customerEmail: String): Ticket {
        val customer: Customer = customerRepository.getReferenceById(customerEmail)
        val product: Product = productRepository.getReferenceById(ean)

        val newTicketDTO = TicketDTO(null, description, "OPEN", null,
            LocalDate.now().toString(), customer, null, product, null)
        return ticketRepository.save(newTicketDTO.toTicket())
    }
}