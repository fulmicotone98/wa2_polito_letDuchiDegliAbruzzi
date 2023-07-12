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
import wa2.polito.it.letduchidegliabruzzi.server.controller.body.ProductBodyID
import wa2.polito.it.letduchidegliabruzzi.server.controller.body.ProductRequestBody
import wa2.polito.it.letduchidegliabruzzi.server.controller.body.ProductResponseBody
import wa2.polito.it.letduchidegliabruzzi.server.controller.httpexception.ConstraintViolationException
import wa2.polito.it.letduchidegliabruzzi.server.controller.httpexception.DuplicateProductException
import wa2.polito.it.letduchidegliabruzzi.server.controller.httpexception.ProductNotFoundException
import wa2.polito.it.letduchidegliabruzzi.server.dal.authDao.UserServiceImpl
import wa2.polito.it.letduchidegliabruzzi.server.dal.dao.product.ProductService
import java.security.Principal

@Validated
@RestController
@Observed
@Slf4j
class ProductController(private val productService: ProductService, private val userService: UserServiceImpl,) {

    private val log: Logger = LoggerFactory.getLogger(ProductController::class.java)
    @GetMapping("/API/products")
    fun getAll(): List<ProductResponseBody>{
        val p = productService.getAll()
        return p.map { ProductResponseBody(it.ean,it.name,it.brand,it.customer.username) }
    }

    @GetMapping("/API/products/{ean}")
    fun getProduct(@PathVariable @NotBlank @Pattern(regexp = "^[A-Za-z0-9]+\$", message = "The Ean should be alphanumeric") ean: String): ProductResponseBody {
        val p = productService.getProduct(ean)
        if(p==null) {
            log.error("Get product Error: Product not found with ean $ean")
            throw ProductNotFoundException("Product not found")
        }
        return ProductResponseBody(p.ean,p.name,p.brand,p.customer.username)
    }

    @PostMapping("/API/products")
    @ResponseStatus(HttpStatus.CREATED)
    fun addProduct(@Valid @RequestBody body: ProductRequestBody, br: BindingResult, principal: Principal): ProductBodyID{
        // Check if the body is valid
        if(br.hasErrors()) {
            log.error("Add product error: Body validation failed with error ${br.allErrors}")
            throw ConstraintViolationException("Body validation failed")
        }

        // Check if the product already exists
        if(productService.getProduct(body.ean) != null){
            throw DuplicateProductException("Product already exists with ean: ${body.ean}")
        }

        val username = principal.name
        val product = productService.addProduct(body.ean, body.brand,body.name,username)
        log.info("New product with ean ${body.ean} added correctly")
        return ProductBodyID(product.ean)
    }

    // TODO(Adding API for getting all the user product of an user)
    // Note that the logged user can be retrieved from Principal instance (see POST /API/products)
}