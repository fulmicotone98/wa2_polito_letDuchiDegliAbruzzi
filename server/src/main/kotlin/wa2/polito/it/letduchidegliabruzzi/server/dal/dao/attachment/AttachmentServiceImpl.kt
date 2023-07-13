package wa2.polito.it.letduchidegliabruzzi.server.dal.dao.attachment

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import wa2.polito.it.letduchidegliabruzzi.server.dal.dao.chat.ChatService
import wa2.polito.it.letduchidegliabruzzi.server.dal.dao.chat.toChat
import wa2.polito.it.letduchidegliabruzzi.server.dal.dao.message.MessageService
import wa2.polito.it.letduchidegliabruzzi.server.dal.dao.message.toMessage
import wa2.polito.it.letduchidegliabruzzi.server.dal.dao.ticket.toDTO
import java.time.Instant
import java.time.format.DateTimeFormatter

@Service
class AttachmentServiceImpl(
    private val attachmentRepository: AttachmentRepository,
    private val messageService: MessageService
) : AttachmentService {

    @Transactional(isolation = Isolation.SERIALIZABLE)
    override fun addAttachment(messageID: Int, fileBase64: String): Attachment {
        val message = messageService.getMessage(messageID)
        val newAttachment = AttachmentDTO(null, fileBase64, message!!.toMessage())
        return attachmentRepository.save(newAttachment.toAttachment())
    }

    @Transactional(readOnly = true, isolation = Isolation.SERIALIZABLE)
    override fun getAttachmentsByMessageID(messageID: Int): List<AttachmentDTO> {
        val attachments = attachmentRepository.findAll()
            .filter { it.message.messageID == messageID }
            .map {
                it.toDTO()
            }
        return attachments
    }
}