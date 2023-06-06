package wa2.polito.it.letduchidegliabruzzi.server.entity.employee

interface EmployeeService {
    fun addEmployee(email: String, name: String, role: String, surname: String): Employee
    fun getEmployeeByID(id: Int): EmployeeDTO?

    fun getEmployeeByEmail(email: String): EmployeeDTO?
}