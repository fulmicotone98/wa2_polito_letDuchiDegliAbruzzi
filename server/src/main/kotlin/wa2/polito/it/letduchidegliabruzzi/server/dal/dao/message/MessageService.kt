package wa2.polito.it.letduchidegliabruzzi.server.dal.dao.message

interface MessageService {
    fun addMessage(chatID: Int, senderUsername: String, text: String) :Message
    fun getMessagesByChatID(chatID: Int): List<MessageDTO>

    fun getMessage(id: Int): MessageDTO?
}