package wa2.polito.it.letduchidegliabruzzi.server.attachment

import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import wa2.polito.it.letduchidegliabruzzi.server.message.Message

class AttachmentDTO(
    @field:Id val attachmentID: Int? = null,
    @field:NotBlank @field:NotNull val path: String = "",
    @field:NotBlank @field:NotNull val message: Message? = null
)

fun Attachment.toDTO(): AttachmentDTO {
    return AttachmentDTO(attachmentID, path, message)
}