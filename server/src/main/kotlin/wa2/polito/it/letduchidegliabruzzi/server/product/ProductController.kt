package wa2.polito.it.letduchidegliabruzzi.server.product

import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import org.springframework.validation.BindingResult
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import wa2.polito.it.letduchidegliabruzzi.server.customer.CustomerDTO
import wa2.polito.it.letduchidegliabruzzi.server.customer.CustomerNotFoundException
import wa2.polito.it.letduchidegliabruzzi.server.customer.CustomerService

class ProductNotFoundException(message: String) : RuntimeException(message)

@Validated
@RestController
class ProductController(private val productService: ProductService, private val customerService: CustomerService) {

    @GetMapping("/API/products")
    fun getAll(): List<ProductDTO>{
        return productService.getAll()
    }

    @GetMapping("/API/products/{ean}")
    fun getProduct(@PathVariable ean: String): ProductDTO? {
        return productService.getProduct(ean)
    }


    @PostMapping("/API/products")
    fun addProduct(@Valid @RequestBody body: BodyObject, br: BindingResult): ProductDTO?{
        var customer: CustomerDTO?

        if(body.customerEmail == null){
            customer = null
        }else {
            customer = customerService.getProfile(body.customerEmail)
            if (customer == null) {
                throw CustomerNotFoundException("Customer not found with Email: ${body.customerEmail}")
            }
        }

        return productService.addProduct(body.ean, body.brand,body.name,body.customerEmail).toDTO()
    }
}

data class BodyObject(
    @field:NotBlank val ean: String,
    @field:NotBlank val name: String,
    @field:NotBlank val brand: String,
    val customerEmail: String?)