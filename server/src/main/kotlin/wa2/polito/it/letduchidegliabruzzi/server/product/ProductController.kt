package wa2.polito.it.letduchidegliabruzzi.server.product

import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import org.springframework.http.HttpStatus
import org.springframework.validation.BindingResult
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import wa2.polito.it.letduchidegliabruzzi.server.customer.CustomerDTO
import wa2.polito.it.letduchidegliabruzzi.server.customer.CustomerNotFoundException
import wa2.polito.it.letduchidegliabruzzi.server.customer.CustomerService
import wa2.polito.it.letduchidegliabruzzi.server.ticket.ConstraintViolationException

class ProductNotFoundException(message: String) : RuntimeException(message)

@Validated
@RestController
class ProductController(private val productService: ProductService, private val customerService: CustomerService) {

    @GetMapping("/API/products")
    fun getAll(): List<ProductResponseBody>{
        val p = productService.getAll()
        return p.map { ProductResponseBody(it.ean,it.name,it.brand,it.customer.email) }
    }

    @GetMapping("/API/products/{ean}")
    fun getProduct(@PathVariable @NotBlank @Pattern(regexp = "^[A-Za-z0-9]+\$", message = "The Ean should be alphanumeric") ean: String): ProductResponseBody? {
        val p = productService.getProduct(ean)?: throw ProductNotFoundException("Product not found")
        return ProductResponseBody(p.ean,p.name,p.brand,p.customer.email)
    }

    @PostMapping("/API/products")
    @ResponseStatus(HttpStatus.CREATED)
    fun addProduct(@Valid @RequestBody body: ProductRequestBody, br: BindingResult): ProductResponseBody?{
        if(br.hasErrors())
            throw ConstraintViolationException("Body validation failed")
        customerService.getProfile(body.customerEmail)
            ?: throw CustomerNotFoundException("Customer not found with Email: ${body.customerEmail}")
        productService.addProduct(body.ean, body.brand,body.name,body.customerEmail)
        return ProductResponseBody(body.ean,null,null,null)
    }
}

data class ProductRequestBody(
    @field:NotBlank @Pattern(regexp = "^[A-Za-z0-9]+\$", message = "The Ean should be alphanumeric") val ean: String,
    @field:NotBlank val name: String,
    @field:NotBlank val brand: String,
    @field:NotBlank @field:Email(message = "The email should be provided in a correct format") val customerEmail: String
)

data class ProductResponseBody(
    @field:NotBlank @field:NotNull val ean: String,
    @field:NotBlank @field:NotNull val name: String? = "",
    @field:NotBlank @field:NotNull val brand: String? = "",
    @field:NotBlank @field:NotNull @field:Email val customerEmail: String? = null
)