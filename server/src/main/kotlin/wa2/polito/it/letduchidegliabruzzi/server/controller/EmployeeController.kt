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

//    @PostMapping("/employee")
//    @ResponseStatus(HttpStatus.CREATED)
//    fun addEmployee(@Valid @RequestBody body: EmployeeBodyRequest, br: BindingResult): EmployeeBodyResponse? {
//        if(br.hasErrors())
//            throw ConstraintViolationException("Body validation failed")
//        if(body.role != "expert" && body.role!="manager")
//            throw EmployeeRoleException("Role must be expert or manager")
//
//        val employee = employeeService.addEmployee(body.email,body.name,body.role,body.surname)
//        return employee.employeeID?.let { EmployeeBodyResponse(it, null,null,null, null) }
//    }

    @GetMapping("/employees/{id}")
    fun getEmployee(@PathVariable @Positive id: Int): EmployeeBodyResponse? {
        val e = employeeService.getEmployeeByID(id)
            ?: throw EmployeeNotFoundException("Employee not found")
        return EmployeeBodyResponse(e.employeeID!!,e.email,e.name,e.role,e.surname)
    }
}
