package wa2.polito.it.letduchidegliabruzzi.server.dal.dao.message

import wa2.polito.it.letduchidegliabruzzi.server.dal.dao.attachment.Attachment
import wa2.polito.it.letduchidegliabruzzi.server.dal.dao.attachment.AttachmentDTO
import wa2.polito.it.letduchidegliabruzzi.server.dal.dao.attachment.toAttachment
import wa2.polito.it.letduchidegliabruzzi.server.dal.dao.attachment.toDTO
import wa2.polito.it.letduchidegliabruzzi.server.dal.dao.chat.Chat
import wa2.polito.it.letduchidegliabruzzi.server.dal.dao.chat.ChatDTO
import wa2.polito.it.letduchidegliabruzzi.server.dal.dao.chat.toChat
import wa2.polito.it.letduchidegliabruzzi.server.dal.dao.chat.toDTO
import wa2.polito.it.letduchidegliabruzzi.server.dal.dao.ticket.Ticket
import java.sql.Timestamp

data class MessageDTO(
    val messageID: Int?,
    val chat: ChatDTO,
    val senderUsername: String,
    val text: String,
    val createdAt: String,
    var attachments: List<AttachmentDTO>
)

fun Message.toDTO(): MessageDTO {
    return MessageDTO(messageID, chat.toDTO(), senderUsername, text, createdAt, attachments.map { it.toDTO() })
}

fun MessageDTO.toMessage() :Message{
    return Message(messageID,text,senderUsername,createdAt,chat.toChat(),attachments.map { it.toAttachment() })
}