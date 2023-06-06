package wa2.polito.it.letduchidegliabruzzi.server.controller

import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import org.springframework.http.HttpStatus
import org.springframework.validation.BindingResult
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import wa2.polito.it.letduchidegliabruzzi.server.controller.body.EmployeeBodyRequest
import wa2.polito.it.letduchidegliabruzzi.server.controller.body.EmployeeBodyResponse
import wa2.polito.it.letduchidegliabruzzi.server.controller.httpexception.ConstraintViolationException
import wa2.polito.it.letduchidegliabruzzi.server.controller.httpexception.EmployeeNotFoundException
import wa2.polito.it.letduchidegliabruzzi.server.controller.httpexception.EmployeeRoleException
import wa2.polito.it.letduchidegliabruzzi.server.entity.employee.EmployeeService

@Validated
@RestController
@RequestMapping("/API")
class EmployeeController(private val employeeService: EmployeeService) {

    @GetMapping("/employees/{id}")
    fun getEmployee(@PathVariable @Positive id: Int): EmployeeBodyResponse? {
        val e = employeeService.getEmployeeByID(id)
            ?: throw EmployeeNotFoundException("Employee not found")
        return EmployeeBodyResponse(e.employeeID!!,e.email,e.name,e.role,e.surname)
    }
}
