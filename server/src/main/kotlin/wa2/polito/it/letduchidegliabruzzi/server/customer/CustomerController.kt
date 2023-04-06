package wa2.polito.it.letduchidegliabruzzi.server.customer

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

class CustomerNotFoundException(message: String) : RuntimeException(message)
class DuplicateCustomerException(message: String) : RuntimeException(message)

@RestController
class CustomerController(private val customerService: CustomerService) {

    @GetMapping("/API/profiles/{email}")
    fun getProfile(@PathVariable email: String): CustomerDTO? {
        return customerService.getProfile(email)
            ?: throw CustomerNotFoundException("Customer not found with Email: $email")
    }

    @PostMapping("/API/profiles")
    fun addProfile(@RequestBody customerDTO: CustomerDTO): CustomerDTO? {
        if (customerService.getProfile(customerDTO.email) != null) {
            throw DuplicateCustomerException("Customer already exists with Email: ${customerDTO.email}")
        }
        return customerService.addProfile(customerDTO).toDTO()
    }

    @PutMapping("/API/profiles/{email}")
    fun updateProfile(@PathVariable email: String, @RequestBody newCustomerDTO: CustomerDTO): CustomerDTO? {
        val customerForUpdate: CustomerDTO? = customerService.getProfile(email)
        if (customerForUpdate != null) {
            return customerService.updateProfile(customerForUpdate, newCustomerDTO).toDTO()
        } else {
            throw CustomerNotFoundException("Customer not found with Email: $email")
        }
    }

}