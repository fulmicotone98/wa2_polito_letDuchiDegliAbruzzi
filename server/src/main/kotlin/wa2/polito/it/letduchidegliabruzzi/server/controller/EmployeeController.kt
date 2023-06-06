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
class EmployeeController(private val employeeService: EmployeeService) {

    private val log: Logger = LoggerFactory.getLogger(ProductController::class.java)

    @PostMapping("/API/employee")
    @ResponseStatus(HttpStatus.CREATED)
    fun addEmployee(@Valid @RequestBody body: EmployeeBodyRequest, br: BindingResult): EmployeeBodyResponse? {
        if(br.hasErrors()) {
            log.error("Error adding a new Employee: Body validation failed with error ${br.allErrors}")
            throw ConstraintViolationException("Body validation failed")
        }
        if(body.role != "expert" && body.role!="manager") {
            log.error("Error adding a new Employee: Role must be Expert or Manager")
            throw EmployeeRoleException("Role must be expert or manager")
        }
        val employee = employeeService.addEmployee(body.email,body.name,body.role,body.surname)
        log.info("Correctly added a new employee with id ${employee.employeeID}")
        return employee.employeeID?.let { EmployeeBodyResponse(it, null,null,null, null) }
    }

    @GetMapping("/API/employees/{id}")
    fun getEmployee(@PathVariable @Positive id: Int): EmployeeBodyResponse? {
        val e = employeeService.getEmployeeByID(id)
        if(e==null){
            log.error("Employee not found with id $id")
            throw EmployeeNotFoundException("Employee not found")
        }
        return EmployeeBodyResponse(e.employeeID!!,e.email,e.name,e.role,e.surname)
    }
}
