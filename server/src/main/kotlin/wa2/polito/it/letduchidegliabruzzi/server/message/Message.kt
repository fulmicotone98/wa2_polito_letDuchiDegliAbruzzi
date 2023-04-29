package wa2.polito.it.letduchidegliabruzzi.server.message

import jakarta.persistence.*
import wa2.polito.it.letduchidegliabruzzi.server.attachment.Attachment
import wa2.polito.it.letduchidegliabruzzi.server.chat.Chat
import wa2.polito.it.letduchidegliabruzzi.server.employee_customer.EmployeeAndCustomer
import java.sql.Timestamp


@Entity
@Table(name = "message")
class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val messageID :Int? = null
    @ManyToOne
    @JoinColumn(name = "chatID")
    val chat :Chat? = null
    @ManyToOne
    @JoinColumn(name = "senderID")
    val sender :EmployeeAndCustomer? = null
    val text :String = ""
    val timestamp :Timestamp = Timestamp(System.currentTimeMillis())
    @OneToMany(mappedBy = "message")
    var attachments: List<Attachment>? = null
}