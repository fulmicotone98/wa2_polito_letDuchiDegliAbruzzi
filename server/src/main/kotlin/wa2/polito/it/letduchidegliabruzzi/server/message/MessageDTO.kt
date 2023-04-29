package wa2.polito.it.letduchidegliabruzzi.server.message

import jakarta.persistence.*
import wa2.polito.it.letduchidegliabruzzi.server.attachment.Attachment
import wa2.polito.it.letduchidegliabruzzi.server.chat.Chat
import wa2.polito.it.letduchidegliabruzzi.server.employee_customer.EmployeeAndCustomer
import java.sql.Timestamp

class MessageDTO(
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