package wa2.polito.it.letduchidegliabruzzi.server.dal.dao.message

import wa2.polito.it.letduchidegliabruzzi.server.dal.dao.attachment.Attachment
import wa2.polito.it.letduchidegliabruzzi.server.dal.dao.chat.Chat
import java.sql.Timestamp

data class MessageDTO(
    val messageID: Int?,
    val chat: Chat,
    val senderUsername: String,
    val text: String,
    val createdAt: String,
    var attachments: List<Attachment>
)

fun Message.toDTO(): MessageDTO {
    return MessageDTO(messageID, chat, senderUsername, text, createdAt, attachments)
}

fun MessageDTO.toMessage() :Message{
    return Message(messageID,text,senderUsername,createdAt,chat,attachments)
}