package wa2.polito.it.letduchidegliabruzzi.server.dal.dao.status_history

import wa2.polito.it.letduchidegliabruzzi.server.dal.dao.ticket.Ticket

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