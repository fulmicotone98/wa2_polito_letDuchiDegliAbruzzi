package wa2.polito.it.letduchidegliabruzzi.server.dal.dao.message

interface MessageService {
    fun pushMessage(chatID: Int, senderID: Int, text: String)
}