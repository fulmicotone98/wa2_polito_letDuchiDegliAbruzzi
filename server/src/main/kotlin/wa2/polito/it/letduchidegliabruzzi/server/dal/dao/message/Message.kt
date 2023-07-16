package wa2.polito.it.letduchidegliabruzzi.server.dal.dao.message

import jakarta.persistence.*
import wa2.polito.it.letduchidegliabruzzi.server.dal.dao.attachment.Attachment
import wa2.polito.it.letduchidegliabruzzi.server.dal.dao.chat.Chat
import java.sql.Timestamp


@Entity
@Table(name = "message")
class Message (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val messageID :Int? = null,
    val text :String,
    val senderUsername: String,
    val createdAt: String,
    @ManyToOne @JoinColumn(name = "chatID") val chat : Chat,
    @OneToMany(mappedBy = "messageID") val attachments: List<Attachment>?
)