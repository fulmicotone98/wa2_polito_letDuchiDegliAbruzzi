package wa2.polito.it.letduchidegliabruzzi.server.employee

import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

class EmployeeDTO(
    val employeeID: Int? = null,
    val email: String = "",
    val name: String = "",
    var surname: String = "",
    var role: String = "expert"
)

fun Employee.toDTO(): EmployeeDTO {
    return EmployeeDTO(employeeID, email, name, surname, role)
}
