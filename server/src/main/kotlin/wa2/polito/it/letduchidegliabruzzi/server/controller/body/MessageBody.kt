package wa2.polito.it.letduchidegliabruzzi.server.controller.body

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive


data class MessageBodyResponse(
    @field:Positive val messageID: Int?,
    @field:Positive val chatID: Int?,
    @field:NotBlank val text: String,
    @field:NotBlank val createdAt: String,
    @field:NotBlank val senderUsername: String,
    @field:NotBlank val senderFirstname: String,
    @field:NotBlank val senderSurname: String,
)

data class MessageBodyRequest(
    @field:NotNull val chatID: Int,
    @field:NotNull @field:NotBlank val text: String,
    @field:NotNull @field:NotBlank val senderUsername: String,
)