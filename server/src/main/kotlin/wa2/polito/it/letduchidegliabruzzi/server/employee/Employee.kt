package wa2.polito.it.letduchidegliabruzzi.server.employee

import jakarta.persistence.*

@Entity
@Table(name = "employee")
class Employee (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val employeeID: Int? = null,
    val email: String = "",
    val name: String = "",
    var surname: String = "",
    var role: String = "expert",
)