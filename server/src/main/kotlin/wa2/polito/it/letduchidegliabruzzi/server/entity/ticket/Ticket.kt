package wa2.polito.it.letduchidegliabruzzi.server.entity.ticket

import jakarta.persistence.*
import wa2.polito.it.letduchidegliabruzzi.server.entity.customer.Customer
import wa2.polito.it.letduchidegliabruzzi.server.entity.employee.Employee
import wa2.polito.it.letduchidegliabruzzi.server.entity.product.Product
import wa2.polito.it.letduchidegliabruzzi.server.entity.status_history.StatusHistory

@Entity
@Table(name = "ticket")
class Ticket(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val ticketID: Int? = null,
    val description: String,
    val status: String,
    val priority: String? = null,
    val createdAt: String = "",

    @ManyToOne
    @JoinColumn(name = "customerEmail")
    var customer: Customer,

    @ManyToOne
    @JoinColumn(name = "employeeID")
    var employee: Employee? = null,

    @ManyToOne
    @JoinColumn(name = "ean")
    var product: Product,

    @OneToMany(mappedBy = "ticket")
    var statusHistory: List<StatusHistory>? = null
)