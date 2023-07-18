package wa2.polito.it.letduchidegliabruzzi.server.dal.dao.attachment

import jakarta.persistence.*
import wa2.polito.it.letduchidegliabruzzi.server.dal.dao.message.Message


@Entity
@Table(name = "attachment")
class Attachment (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val attachmentID :Int?,
    @Lob
    val fileBase64 :String?,
    val messageID : Int?
)