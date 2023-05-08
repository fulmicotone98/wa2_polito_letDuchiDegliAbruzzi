package wa2.polito.it.letduchidegliabruzzi.server.attachment

import jakarta.persistence.*
import wa2.polito.it.letduchidegliabruzzi.server.message.Message


@Entity
@Table(name = "attachment")
class Attachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val attachmentID :Int? = null
    val path :String = ""
    @ManyToOne
    @JoinColumn(name = "messageID")
    val message : Message? = null
}