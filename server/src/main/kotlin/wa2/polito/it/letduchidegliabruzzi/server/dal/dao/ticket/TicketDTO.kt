package wa2.polito.it.letduchidegliabruzzi.server.dal.dao.ticket

import wa2.polito.it.letduchidegliabruzzi.server.dal.authDao.UserDTO
import wa2.polito.it.letduchidegliabruzzi.server.dal.dao.chat.Chat
import wa2.polito.it.letduchidegliabruzzi.server.dal.dao.chat.ChatDTO
import wa2.polito.it.letduchidegliabruzzi.server.dal.dao.chat.toChat
import wa2.polito.it.letduchidegliabruzzi.server.dal.dao.chat.toDTO
import wa2.polito.it.letduchidegliabruzzi.server.dal.dao.product.Product
import wa2.polito.it.letduchidegliabruzzi.server.dal.dao.product.ProductDTO
import wa2.polito.it.letduchidegliabruzzi.server.dal.dao.product.toDTO
import wa2.polito.it.letduchidegliabruzzi.server.dal.dao.product.toProduct
import wa2.polito.it.letduchidegliabruzzi.server.dal.dao.status_history.StatusHistoryDTO
import wa2.polito.it.letduchidegliabruzzi.server.dal.dao.status_history.toDTO
import wa2.polito.it.letduchidegliabruzzi.server.dal.dao.status_history.toStatusHistory

data class TicketDTO(
    val ticketID: Int?,
    val description: String,
    val status: String,
    val priority: String?,
    val createdAt: String,
    val customer: UserDTO,
    val employee: UserDTO?,
    val product: ProductDTO,
    val statusHistory: List<StatusHistoryDTO>,
    var chat: ChatDTO?
)

fun Ticket.toDTO(customer: UserDTO, employee: UserDTO?, statusHistory: List<StatusHistoryDTO>,chat: ChatDTO?): TicketDTO {
    return TicketDTO(
        ticketID,
        description,
        status,
        priority,
        createdAt,
        customer,
        employee,
        product.toDTO(customer),
        statusHistory,
        chat
    )
}

fun TicketDTO.toTicket(): Ticket {
    return Ticket(
        ticketID,
        description,
        status,
        priority,
        createdAt,
        customer.username,
        employee?.username,
        product.toProduct(),
        chat?.toChat()
    )
}