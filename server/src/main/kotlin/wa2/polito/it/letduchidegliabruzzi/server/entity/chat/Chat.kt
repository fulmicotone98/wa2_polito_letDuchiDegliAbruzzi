package wa2.polito.it.letduchidegliabruzzi.server.entity.chat

import jakarta.persistence.*
import wa2.polito.it.letduchidegliabruzzi.server.entity.customer.Customer
import wa2.polito.it.letduchidegliabruzzi.server.entity.employee.Employee
import wa2.polito.it.letduchidegliabruzzi.server.entity.ticket.Ticket


@Entity
@Table(name = "chat")
class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val chatID :Int? = null
    @ManyToOne
    @JoinColumn(name = "employeeID")
    val employee : Employee? = null
    @ManyToOne
    @JoinColumn(name = "customer_email")
    val customer : Customer? = null
    @OneToOne
    @JoinColumn(name = "ticketID")
    val ticket : Ticket? = null
}