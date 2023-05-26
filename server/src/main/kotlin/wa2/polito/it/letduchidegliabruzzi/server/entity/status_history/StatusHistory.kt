package wa2.polito.it.letduchidegliabruzzi.server.entity.status_history

import jakarta.persistence.*
import wa2.polito.it.letduchidegliabruzzi.server.entity.ticket.Ticket


@Entity
@Table(name = "status_history")
class StatusHistory(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val statusID :Int?,
    @ManyToOne
    @JoinColumn(name = "ticketID")
    val ticket : Ticket,
    val createdAt :String = "",
    val status: String
)
