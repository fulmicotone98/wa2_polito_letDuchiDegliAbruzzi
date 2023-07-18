package wa2.polito.it.letduchidegliabruzzi.server.controller.body

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import org.springframework.web.multipart.MultipartFile
import wa2.polito.it.letduchidegliabruzzi.server.dal.dao.attachment.Attachment
import wa2.polito.it.letduchidegliabruzzi.server.dal.dao.attachment.AttachmentDTO


data class MessageBodyResponse(
    @field:Positive val messageID: Int?,
    @field:Positive val chatID: Int?,
    @field:NotBlank val text: String,
    @field:NotBlank val createdAt: String,
    @field:NotBlank val senderUsername: String,
    @field:NotBlank val senderFirstname: String,
    @field:NotBlank val senderSurname: String,
    val attachments :List<AttachmentDTO>?
)

data class MessageBodyRequest(
    @field:NotNull val chatID: Int,
    @field:NotNull @field:NotBlank val text: String,
    val attachments: List<String>?
)