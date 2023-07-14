package wa2.polito.it.letduchidegliabruzzi.server.dal.dao.chat

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import wa2.polito.it.letduchidegliabruzzi.server.dal.dao.ticket.TicketService
import wa2.polito.it.letduchidegliabruzzi.server.dal.dao.ticket.toTicket

@Service
class ChatServiceImpl(
    private val chatRepository: ChatRepository,
    private val ticketService: TicketService
) : ChatService {
    @Transactional(readOnly = true, isolation = Isolation.SERIALIZABLE)
    override fun getChatInfo(chatID: Int): ChatDTO? {
        val chat = chatRepository.findByIdOrNull(chatID)
        return chat?.toDTO(chat.ticket)
    }

    @Transactional(readOnly = true, isolation = Isolation.SERIALIZABLE)
    override fun getChatByTicketID(ticketID: Int): ChatDTO? {
        val ticket = ticketService.getTicket(ticketID)
        return ticket?.chat
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    override fun addChat(ticketID: Int): Chat {
        val ticket = ticketService.getTicket(ticketID)
        val newChat = ChatDTO(null, ticket!!.toTicket())
        return chatRepository.save(newChat.toChat())
    }
}