package wa2.polito.it.letduchidegliabruzzi.server.dal.dao.message

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import wa2.polito.it.letduchidegliabruzzi.server.dal.dao.chat.ChatRepository
import wa2.polito.it.letduchidegliabruzzi.server.dal.dao.chat.ChatService
import wa2.polito.it.letduchidegliabruzzi.server.dal.dao.chat.toChat
import wa2.polito.it.letduchidegliabruzzi.server.dal.dao.ticket.TicketDTO
import wa2.polito.it.letduchidegliabruzzi.server.dal.dao.ticket.TicketService
import wa2.polito.it.letduchidegliabruzzi.server.dal.dao.ticket.toDTO
import wa2.polito.it.letduchidegliabruzzi.server.dal.dao.ticket.toTicket
import java.time.Instant
import java.time.format.DateTimeFormatter

@Service
class MessageServiceImpl(
    private val messageRepository: MessageRepository,
    private val chatService: ChatService,
    private val ticketService: TicketService
) : MessageService {
    @Transactional(isolation = Isolation.SERIALIZABLE)
    override fun addMessage(chatID: Int, senderUsername: String, text: String): Message {
        val chat = chatService.getChatInfo(chatID)
        val timestamp = DateTimeFormatter.ISO_INSTANT.format(Instant.now()).toString()
        val newMessage = MessageDTO(null, chat!!, senderUsername, text, timestamp, listOf())
        return messageRepository.save(newMessage.toMessage())

    }

    @Transactional(readOnly = true, isolation = Isolation.SERIALIZABLE)
    override fun getMessage(id: Int): MessageDTO? {
        val message = messageRepository.findByIdOrNull(id) ?: return null
        return message.toDTO()
    }

    @Transactional(readOnly = true, isolation = Isolation.SERIALIZABLE)
    override fun getMessagesByChatID(chatID: Int): List<MessageDTO> {
        val messages = messageRepository.findAll()
            .filter { it.chat.chatID == chatID }
            .map {
                it.toDTO()
            }
        return messages
    }
}