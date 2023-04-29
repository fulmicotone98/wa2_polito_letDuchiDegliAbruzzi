package wa2.polito.it.letduchidegliabruzzi.server.chat

import jakarta.persistence.*
import wa2.polito.it.letduchidegliabruzzi.server.customer.Customer
import wa2.polito.it.letduchidegliabruzzi.server.employee.Employee
import wa2.polito.it.letduchidegliabruzzi.server.ticket.Ticket


@Entity
@Table(name = "chat")
class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val chatID :Int? = null
    @ManyToOne
    @JoinColumn(name = "employeeID")
    val employee :Employee? = null
    @ManyToOne
    @JoinColumn(name = "customerEmail")
    val customer :Customer? = null
    @OneToOne
    @JoinColumn(name = "ticketID")
    val ticket :Ticket? = null
}