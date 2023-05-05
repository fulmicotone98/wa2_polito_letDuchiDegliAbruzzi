package wa2.polito.it.letduchidegliabruzzi.server.employee

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

fun EmployeeDTO.toEmployee(): Employee {
    return Employee(employeeID, email, name, surname, role)
}