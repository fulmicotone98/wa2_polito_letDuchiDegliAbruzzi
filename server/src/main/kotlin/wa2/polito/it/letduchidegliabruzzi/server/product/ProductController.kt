package wa2.polito.it.letduchidegliabruzzi.server.product

import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
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
    fun getAll(): List<ProductResponseBody>{
        val p = productService.getAll()
        return p.map { ProductResponseBody(it.ean,it.name,it.brand,it.customer?.email) }
    }

    @GetMapping("/API/products/{ean}")
    fun getProduct(@PathVariable @NotBlank ean: String): ProductResponseBody? {
        val p = productService.getProduct(ean)?: throw ProductNotFoundException("Product not found")
        return ProductResponseBody(p.ean,p.name,p.brand,p.customer?.email)
    }

    @PostMapping("/API/products")
    fun addProduct(@Valid @RequestBody body: BodyObject, br: BindingResult): ProductDTO?{
        val customer: CustomerDTO?

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

data class ProductResponseBody(
    @field:NotBlank @field:NotNull val ean: String,
    @field:NotBlank @field:NotNull val name: String,
    @field:NotBlank @field:NotNull val brand: String,
    val customerEmail: String?
)