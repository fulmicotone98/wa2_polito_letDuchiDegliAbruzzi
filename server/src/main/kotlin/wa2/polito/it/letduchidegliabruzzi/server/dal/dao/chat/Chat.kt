package wa2.polito.it.letduchidegliabruzzi.server.dal.dao.chat

import jakarta.persistence.*
import wa2.polito.it.letduchidegliabruzzi.server.dal.dao.ticket.Ticket


@Entity
@Table(name = "chat")
class Chat (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val chatID :Int?,
    @OneToOne @JoinColumn(name = "ticketID") val ticket : Ticket
)