package wa2.polito.it.letduchidegliabruzzi.server.dal.dao.status_history

import wa2.polito.it.letduchidegliabruzzi.server.dal.dao.ticket.Ticket

class StatusHistoryDTO(
    val statusID: Int?,
    val ticketID: Int,
    val createdAt: String,
    val status: String
)

fun StatusHistory.toDTO(ticketID: Int): StatusHistoryDTO {
    return StatusHistoryDTO(statusID, ticketID, createdAt, status)
}

fun StatusHistoryDTO.toStatusHistory(ticket: Ticket): StatusHistory {
    return StatusHistory(statusID, ticket, createdAt, status)
}