package wa2.polito.it.letduchidegliabruzzi.server.dal.dao.chat


import wa2.polito.it.letduchidegliabruzzi.server.dal.authDao.UserDTO
import wa2.polito.it.letduchidegliabruzzi.server.dal.dao.ticket.TicketDTO
import wa2.polito.it.letduchidegliabruzzi.server.dal.dao.ticket.Ticket
import wa2.polito.it.letduchidegliabruzzi.server.dal.dao.ticket.toDTO
import wa2.polito.it.letduchidegliabruzzi.server.dal.dao.ticket.toTicket

data class ChatDTO(
    val chatID: Int?
)

fun Chat.toDTO(ticket: Ticket): ChatDTO {
    return ChatDTO(chatID)
}

fun ChatDTO.toChat(ticket: TicketDTO): Chat{
    return Chat(chatID,ticket.toTicket())
}