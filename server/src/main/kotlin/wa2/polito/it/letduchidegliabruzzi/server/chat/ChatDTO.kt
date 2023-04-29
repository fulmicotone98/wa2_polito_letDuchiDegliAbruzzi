package wa2.polito.it.letduchidegliabruzzi.server.chat


import wa2.polito.it.letduchidegliabruzzi.server.customer.Customer
import wa2.polito.it.letduchidegliabruzzi.server.employee.Employee
import wa2.polito.it.letduchidegliabruzzi.server.ticket.Ticket

class ChatDTO(
    val chatID: Int? = null,
    val employee: Employee? = null,
    val customer: Customer? = null,
    val ticket: Ticket? = null
)

fun Chat.toDTO(): ChatDTO {
    return ChatDTO(chatID, employee, customer, ticket)
}