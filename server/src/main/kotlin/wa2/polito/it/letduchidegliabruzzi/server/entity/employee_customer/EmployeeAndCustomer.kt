package wa2.polito.it.letduchidegliabruzzi.server.entity.employee_customer

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "employee_and_customer")
class EmployeeAndCustomer {
    @Id
    //save the email or id of customer or employee as string
    val id: String = ""
    //must be employee or customer
    val source :String = ""
}