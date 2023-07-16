package wa2.polito.it.letduchidegliabruzzi.server.dal.dao.attachment

import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import wa2.polito.it.letduchidegliabruzzi.server.dal.dao.message.Message

class AttachmentDTO(
    @field:Id val attachmentID: Int? = null,
    @field:NotBlank @field:NotNull val fileBase64: String?,
    @field:NotBlank @field:NotNull val messageId: Int?
)

fun Attachment.toDTO(): AttachmentDTO {
    return AttachmentDTO(attachmentID, fileBase64, messageID)
}

fun AttachmentDTO.toAttachment(): Attachment {
    return Attachment(attachmentID, fileBase64, messageId)
}