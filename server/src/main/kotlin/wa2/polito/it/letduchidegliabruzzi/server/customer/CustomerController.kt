package wa2.polito.it.letduchidegliabruzzi.server.customer

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class CustomerController(private val customerService: CustomerService) {

    @GetMapping(" /API/profiles/{email}")
    fun getProfile(@PathVariable email: String): CustomerDTO? {
        return customerService.getProfile(email)
    }

}