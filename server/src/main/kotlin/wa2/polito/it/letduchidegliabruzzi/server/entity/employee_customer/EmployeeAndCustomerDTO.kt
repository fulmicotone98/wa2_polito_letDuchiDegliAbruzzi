package wa2.polito.it.letduchidegliabruzzi.server.entity.employee_customer

import jakarta.persistence.Id

class EmployeeAndCustomerDTO(
    val id: String = "",
    val source: String = ""
)

fun EmployeeAndCustomer.toDTO(): EmployeeAndCustomerDTO {
    return EmployeeAndCustomerDTO(id, source)
}