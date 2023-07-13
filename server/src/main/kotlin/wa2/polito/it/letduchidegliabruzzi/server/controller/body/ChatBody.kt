package wa2.polito.it.letduchidegliabruzzi.server.controller.body

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive


data class ChatBodyResponse(
    @field:Positive val chatID: Int?,
    @field:Positive val ticketID: Int?,
)

data class ChatBodyRequest(
    @field:NotNull @field:NotBlank val ticketID: Int,
)
