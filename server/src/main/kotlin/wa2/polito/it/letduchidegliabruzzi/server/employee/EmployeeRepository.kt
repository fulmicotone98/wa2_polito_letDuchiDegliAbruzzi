package wa2.polito.it.letduchidegliabruzzi.server.employee

import org.springframework.data.jpa.repository.JpaRepository

interface EmployeeRepository: JpaRepository<Employee, Int> {
}