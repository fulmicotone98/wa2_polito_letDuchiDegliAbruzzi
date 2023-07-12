package wa2.polito.it.letduchidegliabruzzi.server.dal.dao.message

import wa2.polito.it.letduchidegliabruzzi.server.dal.dao.chat.ChatRepository

class MessageServiceImpl(private val messageRepository: wa2.polito.it.letduchidegliabruzzi.server.dal.dao.message.MessageRepository,
                         private val chatRepository: ChatRepository
): wa2.polito.it.letduchidegliabruzzi.server.dal.dao.message.MessageService {

    override fun pushMessage(chatID: Int, senderID: Int, text: String) {
        //val chat: Chat = chatRepository.getReferenceById(chatID)
        TODO()
    }
}