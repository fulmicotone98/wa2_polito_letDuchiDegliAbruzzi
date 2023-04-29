package wa2.polito.it.letduchidegliabruzzi.server.status_history

import jakarta.persistence.*
import wa2.polito.it.letduchidegliabruzzi.server.ticket.Ticket


@Entity
@Table(name = "status_history")
class StatusHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val statusID :Int? = null
    @ManyToOne
    @JoinColumn(name = "ticketID")
    val ticket :Ticket? = null
    val createdAt :String = ""
}