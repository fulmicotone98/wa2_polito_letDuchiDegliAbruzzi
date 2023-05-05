package wa2.polito.it.letduchidegliabruzzi.server.employee

interface EmployeeService {
    fun addEmployee(email: String, name: String, role: String, surname: String): Employee
    fun getEmployeeByID(id: Int): EmployeeDTO?
}