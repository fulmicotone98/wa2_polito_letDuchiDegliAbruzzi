package wa2.polito.it.letduchidegliabruzzi.server.entity.chat


import wa2.polito.it.letduchidegliabruzzi.server.entity.customer.Customer
import wa2.polito.it.letduchidegliabruzzi.server.entity.employee.Employee
import wa2.polito.it.letduchidegliabruzzi.server.entity.ticket.Ticket

data class ChatDTO(
    val chatID: Int? = null,
    val employee: Employee? = null,
    val customer: Customer? = null,
    val ticket: Ticket? = null
)

fun Chat.toDTO(): ChatDTO {
    return ChatDTO(chatID, employee, customer, ticket)
}