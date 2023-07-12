package wa2.polito.it.letduchidegliabruzzi.server.dal.dao.attachment

import jakarta.persistence.*
import wa2.polito.it.letduchidegliabruzzi.server.dal.dao.message.Message


@Entity
@Table(name = "attachment")
class Attachment (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val attachmentID :Int?,
    val path :String,
    @ManyToOne @JoinColumn(name = "messageID") val message : Message
)