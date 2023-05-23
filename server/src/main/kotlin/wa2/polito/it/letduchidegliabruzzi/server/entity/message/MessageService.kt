package wa2.polito.it.letduchidegliabruzzi.server.entity.message

interface MessageService {
    fun pushMessage(chatID: Int, senderID: Int, text: String)
}