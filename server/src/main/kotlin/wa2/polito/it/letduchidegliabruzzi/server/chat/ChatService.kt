package wa2.polito.it.letduchidegliabruzzi.server.chat

interface ChatService {
    fun getChatInfo(chatID: Int?) : ChatDTO?
}