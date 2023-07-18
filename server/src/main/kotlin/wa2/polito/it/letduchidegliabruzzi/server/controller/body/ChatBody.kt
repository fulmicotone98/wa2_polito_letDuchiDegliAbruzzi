package wa2.polito.it.letduchidegliabruzzi.server.controller.body

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import org.springframework.web.multipart.MultipartFile
import java.io.File


data class ChatBodyResponse(
    @field:Positive val chatID: Int?,
)

data class ChatBodyRequest(
    @field:NotNull @field:Positive val ticketID: Int,
    @field:NotNull @field:NotBlank val message: String,
    val files : List<String>?
)
