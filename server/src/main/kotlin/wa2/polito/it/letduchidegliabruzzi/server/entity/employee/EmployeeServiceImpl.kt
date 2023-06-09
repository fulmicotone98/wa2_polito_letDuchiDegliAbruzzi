package wa2.polito.it.letduchidegliabruzzi.server.entity.employee

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional

@Service
class EmployeeServiceImpl(private val employeeRepository: EmployeeRepository) : EmployeeService {

    @Transactional(isolation = Isolation.SERIALIZABLE)
    override fun addEmployee(email: String, name: String, role: String, surname: String): Employee {
        val employeeDTO = EmployeeDTO(null, email, name, surname, role)
        return employeeRepository.save(employeeDTO.toEmployee())
    }

    @Transactional(readOnly = true, isolation = Isolation.SERIALIZABLE)
    override fun getEmployeeByID(id: Int): EmployeeDTO? {
        return employeeRepository.findAll().find { it.employeeID == id }?.toDTO()
    }

    @Transactional(readOnly = true, isolation = Isolation.SERIALIZABLE)
    override fun getEmployeeByEmail(email: String): EmployeeDTO? {
        return employeeRepository.findAll().find{ it.email == email}?.toDTO()
    }
}