package wa2.polito.it.letduchidegliabruzzi.server.controller

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import wa2.polito.it.letduchidegliabruzzi.server.controller.body.CustomerRequestBody
import wa2.polito.it.letduchidegliabruzzi.server.controller.body.CustomerResponseBody
import wa2.polito.it.letduchidegliabruzzi.server.controller.httpexception.ConstraintViolationException
import wa2.polito.it.letduchidegliabruzzi.server.controller.httpexception.DuplicateCustomerException
import wa2.polito.it.letduchidegliabruzzi.server.controller.httpexception.DuplicateEmployeeException
import wa2.polito.it.letduchidegliabruzzi.server.entity.authentication.UserDTO
import wa2.polito.it.letduchidegliabruzzi.server.entity.customer.CustomerDTO
import wa2.polito.it.letduchidegliabruzzi.server.entity.customer.CustomerService
import wa2.polito.it.letduchidegliabruzzi.server.entity.employee.EmployeeService
import wa2.polito.it.letduchidegliabruzzi.server.security.AuthenticationService
import wa2.polito.it.letduchidegliabruzzi.server.security.CredentialsLogin
import wa2.polito.it.letduchidegliabruzzi.server.security.JwtResponse
import wa2.polito.it.letduchidegliabruzzi.server.service.KeycloakService

@RestController
@RequestMapping("/API")
class KeycloakController(
    private val authenticationService: AuthenticationService,
    private val service: KeycloakService,
    private val employeeService: EmployeeService,
    private val customerService: CustomerService) {

    @PostMapping("/login")
    fun login(@RequestBody credentials: CredentialsLogin): ResponseEntity<Any>{
        val jwt: String? = authenticationService.authenticate(credentials)
        return if(jwt!= null){
            ResponseEntity.ok(JwtResponse(jwt))
        } else{
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
    }
    
    @PostMapping("/employee/createExpert")
    @ResponseStatus(HttpStatus.CREATED)
    fun createExpert(@Valid @RequestBody userDTO: UserDTO,br: BindingResult): UserDTO {
        if(br.hasErrors())
            throw ConstraintViolationException("Body validation failed")

        if(employeeService.getEmployeeByEmail(userDTO.emailID)!=null)
            throw DuplicateEmployeeException("Employee already exists with Email: ${userDTO.emailID}")

        val status = service.addUser(userDTO, listOf("Experts_group"))
        employeeService.addEmployee(userDTO.emailID,userDTO.firstName,"expert",userDTO.lastName)
        return userDTO

    }

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    fun signup(@Valid @RequestBody userDTO: UserDTO,br: BindingResult): UserDTO {
        if(br.hasErrors())
            throw ConstraintViolationException("Body validation failed")

        if (customerService.getProfile(userDTO.emailID) != null)
            throw DuplicateCustomerException("Customer already exists with Email: ${userDTO.emailID}")

        val status = service.addUser(userDTO, listOf("Customers_group"))
        customerService.addProfile(CustomerDTO(userDTO.firstName,userDTO.lastName,userDTO.phoneNumber?:"",userDTO.address?:"",userDTO.emailID))
        return userDTO
    }

}