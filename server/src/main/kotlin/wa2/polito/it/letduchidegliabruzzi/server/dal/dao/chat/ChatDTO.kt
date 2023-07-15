package wa2.polito.it.letduchidegliabruzzi.server.dal.dao.chat



data class ChatDTO(
    val chatID: Int?,
    val ticketID: Int
)

fun Chat.toDTO(): ChatDTO {
    return ChatDTO(chatID, ticketID)
}

fun ChatDTO.toChat(): Chat{
    return Chat(chatID,ticketID)
}