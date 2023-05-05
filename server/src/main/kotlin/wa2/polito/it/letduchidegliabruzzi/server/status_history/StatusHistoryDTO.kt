package wa2.polito.it.letduchidegliabruzzi.server.status_history

import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import wa2.polito.it.letduchidegliabruzzi.server.ticket.Ticket

class StatusHistoryDTO(
    val statusID: Int?,
    val ticket: Ticket,
    val createdAt: String,
    val status: String
)

fun StatusHistory.toDTO(): StatusHistoryDTO {
    return StatusHistoryDTO(statusID, ticket, createdAt,status)
}

fun StatusHistoryDTO.toStatusHistory(): StatusHistory {
    return StatusHistory(statusID,ticket,createdAt,status)
}