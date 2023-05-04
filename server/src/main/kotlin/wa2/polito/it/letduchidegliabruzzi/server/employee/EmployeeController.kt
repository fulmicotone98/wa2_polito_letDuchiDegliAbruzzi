package wa2.polito.it.letduchidegliabruzzi.server.employee

import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import org.springframework.validation.BindingResult
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

class EmployeeNotFoundException(message: String): RuntimeException(message)
class ConstraintViolationException(message: String): RuntimeException(message)
class EmployeeRoleException(message: String): RuntimeException(message)
@Validated
@RestController
class EmployeeController(private val employeeService: EmployeeService) {

    @PostMapping("/API/employee")
    fun addEmployee(@Valid @RequestBody body: BodyObject, br: BindingResult): EmployeeDTO? {
        if(br.hasErrors())
            throw ConstraintViolationException("Body validation failed")
        if(body.role != "expert" && body.role!="manager")
            throw EmployeeRoleException("Role must be expert or manager")
        return employeeService.addEmployee(body.email,body.name,body.role,body.surname).toDTO()
    }

    @GetMapping("/API/employees/{id}")
    fun getEmployee(@PathVariable @Positive id: Int): EmployeeDTO? {
        return employeeService.getEmployeeByID(id)
            ?: throw EmployeeNotFoundException("Employee not found")
    }
}

data class BodyObject(
    @field:NotBlank val email: String,
    @field:NotBlank val name: String,
    @field:NotBlank val role: String,
    @field:NotBlank val surname: String
)