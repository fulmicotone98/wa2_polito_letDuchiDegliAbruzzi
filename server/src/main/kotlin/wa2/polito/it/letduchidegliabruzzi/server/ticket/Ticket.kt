package wa2.polito.it.letduchidegliabruzzi.server.ticket

import jakarta.persistence.*
import wa2.polito.it.letduchidegliabruzzi.server.customer.Customer
import wa2.polito.it.letduchidegliabruzzi.server.employee.Employee
import wa2.polito.it.letduchidegliabruzzi.server.product.Product
import wa2.polito.it.letduchidegliabruzzi.server.status_history.StatusHistory

@Entity
@Table(name = "ticket")
class Ticket (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val ticketID: Int? = null,
    val description: String = "",
    val status: String = "",
    val priority: String? = null,
    val createdAt: String = "",

    @ManyToOne
    @JoinColumn(name = "customerEmail")
    var customer: Customer? = null,

    @ManyToOne
    @JoinColumn(name = "employeeID")
    var employee: Employee? = null,

    @ManyToOne
    @JoinColumn(name = "ean")
    var product: Product? = null,

    @OneToMany(mappedBy = "ticket")
    var statusHistory: List<StatusHistory>? = null
)