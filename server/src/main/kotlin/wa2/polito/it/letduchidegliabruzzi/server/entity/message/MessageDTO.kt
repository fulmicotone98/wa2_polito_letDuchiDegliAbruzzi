package wa2.polito.it.letduchidegliabruzzi.server.entity.message

import wa2.polito.it.letduchidegliabruzzi.server.entity.attachment.Attachment
import wa2.polito.it.letduchidegliabruzzi.server.entity.chat.Chat
import wa2.polito.it.letduchidegliabruzzi.server.entity.employee_customer.EmployeeAndCustomer
import java.sql.Timestamp

data class MessageDTO(
    val messageID: Int? = null,
    val chat: Chat? = null,
    val sender: EmployeeAndCustomer? = null,
    val text: String = "",
    val timestamp: Timestamp = Timestamp(System.currentTimeMillis()),
    var attachments: List<Attachment>? = null
)

fun Message.toDTO(): MessageDTO {
    return MessageDTO(messageID, chat, sender, text, timestamp, attachments)
}