package wa2.polito.it.letduchidegliabruzzi.server.entity.chat

interface ChatService {
    fun getChatInfo(chatID: Int?) : ChatDTO?
}