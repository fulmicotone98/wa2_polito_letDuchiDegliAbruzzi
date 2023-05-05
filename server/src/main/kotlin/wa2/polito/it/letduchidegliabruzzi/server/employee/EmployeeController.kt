package wa2.polito.it.letduchidegliabruzzi.server.employee

import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import org.springframework.http.HttpStatus
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
    @ResponseStatus(HttpStatus.CREATED)
    fun addEmployee(@Valid @RequestBody body: BodyObject, br: BindingResult): EmployeeDTO? {
        if(br.hasErrors())
            throw ConstraintViolationException("Body validation failed")
        if(body.role != "expert" && body.role!="manager")
            throw EmployeeRoleException("Role must be expert or manager")

        return employeeService.addEmployee(body.email,body.name,body.role,body.surname).toDTO()
    }

    @GetMapping("/API/employees/{id}")
    fun getEmployee(@PathVariable @Positive id: Int): EmployeeResponseBody? {
        val e = employeeService.getEmployeeByID(id)
            ?: throw EmployeeNotFoundException("Employee not found")
        return EmployeeResponseBody(e.employeeID!!,e.email,e.name,e.role,e.surname)
    }
}

data class BodyObject(
    @field:NotBlank val email: String,
    @field:NotBlank val name: String,
    @field:NotBlank val role: String,
    @field:NotBlank val surname: String
)

data class EmployeeResponseBody(
    @field:Positive @field:NotNull val employeeID: Int = 0,
    @field:NotBlank @field:NotNull @field:Email val email: String = "",
    @field:NotBlank @field:NotNull val name: String = "",
    @field:NotBlank @field:NotNull val role: String = "",
    @field:NotBlank @field:NotNull val surname: String = ""
)