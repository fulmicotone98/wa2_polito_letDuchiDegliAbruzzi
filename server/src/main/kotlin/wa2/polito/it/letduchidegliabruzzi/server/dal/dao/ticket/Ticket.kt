package wa2.polito.it.letduchidegliabruzzi.server.dal.dao.ticket

import jakarta.persistence.*
import wa2.polito.it.letduchidegliabruzzi.server.dal.dao.chat.Chat
import wa2.polito.it.letduchidegliabruzzi.server.dal.dao.product.Product
import wa2.polito.it.letduchidegliabruzzi.server.dal.dao.status_history.StatusHistory

@Entity
@Table(name = "ticket")
class Ticket(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val ticketID: Int?,
    val description: String,
    val status: String,
    val priority: String?,
    val createdAt: String,
    val customerUsername: String,
    val expertUsername: String?,
    @ManyToOne @JoinColumn(name = "ean") val product: Product,
    @OneToMany val statusHistory: List<StatusHistory>,
    @OneToOne @JoinColumn(name = "ticketID") val chat: Chat?
)