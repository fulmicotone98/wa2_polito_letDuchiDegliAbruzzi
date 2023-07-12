package wa2.polito.it.letduchidegliabruzzi.server.dal.dao.chat

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class ChatServiceImpl(private val chatRepository: ChatRepository): ChatService {

    override fun getChatInfo(chatID: Int?): ChatDTO? {
        val chat = chatRepository.findByIdOrNull(chatID)
        return chat?.toDTO(chat.ticket)
    }
}