package wa2.polito.it.letduchidegliabruzzi.server.entity.chat

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class ChatServiceImpl(private val chatRepository: ChatRepository): ChatService {

    override fun getChatInfo(chatID: Int?): ChatDTO? {
        return chatRepository.findByIdOrNull(chatID)?.toDTO()
    }
}