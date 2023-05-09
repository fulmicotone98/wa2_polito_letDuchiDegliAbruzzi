package wa2.polito.it.letduchidegliabruzzi.server.status_history

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