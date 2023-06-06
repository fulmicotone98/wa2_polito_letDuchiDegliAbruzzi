package wa2.polito.it.letduchidegliabruzzi.server.controller

import io.micrometer.observation.annotation.Observed
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import lombok.extern.slf4j.Slf4j
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.validation.BindingResult
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import wa2.polito.it.letduchidegliabruzzi.server.controller.body.ProductRequestBody
import wa2.polito.it.letduchidegliabruzzi.server.controller.body.ProductResponseBody
import wa2.polito.it.letduchidegliabruzzi.server.controller.httpexception.ConstraintViolationException
import wa2.polito.it.letduchidegliabruzzi.server.controller.httpexception.CustomerNotFoundException
import wa2.polito.it.letduchidegliabruzzi.server.controller.httpexception.ProductNotFoundException
import wa2.polito.it.letduchidegliabruzzi.server.entity.customer.CustomerService
import wa2.polito.it.letduchidegliabruzzi.server.entity.product.ProductService

@Validated
@RestController
@Observed
@Slf4j
class ProductController(private val productService: ProductService, private val customerService: CustomerService) {

    private val log: Logger = LoggerFactory.getLogger(ProductController::class.java)
    @GetMapping("/API/products")
    fun getAll(): List<ProductResponseBody>{
        val p = productService.getAll()
        return p.map { ProductResponseBody(it.ean,it.name,it.brand,it.customer.email) }
    }

    @GetMapping("/API/products/{ean}")
    fun getProduct(@PathVariable @NotBlank @Pattern(regexp = "^[A-Za-z0-9]+\$", message = "The Ean should be alphanumeric") ean: String): ProductResponseBody? {
        val p = productService.getProduct(ean)
        if(p==null) {
            log.error("Get product Error: Product not found with ean $ean")
            throw ProductNotFoundException("Product not found")
        }
        return ProductResponseBody(p.ean,p.name,p.brand,p.customer.email)
    }

    @PostMapping("/API/products")
    @ResponseStatus(HttpStatus.CREATED)
    fun addProduct(@Valid @RequestBody body: ProductRequestBody, br: BindingResult): ProductResponseBody?{
        if(br.hasErrors()) {
            log.error("Add product error: Body validation failed with error ${br.allErrors}")
            throw ConstraintViolationException("Body validation failed")
        }
        if(customerService.getProfile(body.customerEmail) == null){
            log.error("Add product error: Customer not found with Email: ${body.customerEmail}")
            throw CustomerNotFoundException("Customer not found with Email: ${body.customerEmail}")
        }
        productService.addProduct(body.ean, body.brand,body.name,body.customerEmail)
        log.info("New product with ean ${body.ean} added correctly")
        return ProductResponseBody(body.ean,null,null,null)
    }
}