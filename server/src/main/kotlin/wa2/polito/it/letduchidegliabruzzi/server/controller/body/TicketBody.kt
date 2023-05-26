package wa2.polito.it.letduchidegliabruzzi.server.controller.body

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive

data class StatusHistoryBodyResponse(
    @field:NotBlank val statusID: Int?,
    @field:NotBlank val ticketID: Int?,
    @field:NotBlank val createdAt: String,
    @field:NotBlank val status: String
)

data class TicketBodyResponse(
    @field:Positive val ticketID: Int?,
    @field:NotBlank val description: String,
    @field:NotBlank val status: String,
    @field:NotBlank val priority: String?,
    @field:NotBlank val createdAt: String,
    @field:NotBlank val productEan: String,
    @field:NotBlank val customerEmail: String,
    val employeeId: Int?
)

data class TicketBodyRequest(
    @field:NotNull @field:NotBlank val ean: String,
    @field:NotNull @field:NotBlank val description: String,
    @field:NotNull @field:NotBlank val customerEmail: String
)

data class AssignTicketBodyRequest(
    @field:NotNull @field:Positive val employeeID: Int,
    @field:NotNull @field:NotBlank val priority: String
)

data class TicketIDBodyResponse(val ticketID: Int?)

data class BodyStatusTicket(@field:NotBlank val status: String)