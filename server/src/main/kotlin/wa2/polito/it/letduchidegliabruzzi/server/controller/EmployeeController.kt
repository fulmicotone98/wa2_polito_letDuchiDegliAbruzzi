package wa2.polito.it.letduchidegliabruzzi.server.controller

import io.micrometer.observation.annotation.Observed
import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import lombok.extern.slf4j.Slf4j
import org.slf4j.Logger
import org.slf4j.LoggerFactory
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
@Observed
@Slf4j
@RequestMapping("/API")
class EmployeeController(private val employeeService: EmployeeService) {

    private val log: Logger = LoggerFactory.getLogger(ProductController::class.java)

    @GetMapping("/employees/{id}")
    fun getEmployee(@PathVariable @Positive id: Int): EmployeeBodyResponse? {
        val e = employeeService.getEmployeeByID(id)
        if(e==null){
            log.error("Employee not found with id $id")
            throw EmployeeNotFoundException("Employee not found")
        }
        return EmployeeBodyResponse(e.employeeID!!,e.email,e.name,e.role,e.surname)
    }
}
