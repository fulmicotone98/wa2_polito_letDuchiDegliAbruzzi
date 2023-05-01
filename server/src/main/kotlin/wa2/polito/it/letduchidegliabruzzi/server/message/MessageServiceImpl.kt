package wa2.polito.it.letduchidegliabruzzi.server.message

import wa2.polito.it.letduchidegliabruzzi.server.chat.ChatRepository

class MessageServiceImpl(private val messageRepository: MessageRepository,
    private val chatRepository: ChatRepository): MessageService {

    override fun pushMessage(chatID: Int, senderID: Int, text: String) {
        //val chat: Chat = chatRepository.getReferenceById(chatID)
        TODO()
    }
}