package wa2.polito.it.letduchidegliabruzzi.server.dal.dao.chat

interface ChatService {
    fun getChatInfo(chatID: Int) : ChatDTO?
    fun getChatByTicketID(ticketID: Int) : ChatDTO?
    fun addChat(ticketID: Int) : Chat
}