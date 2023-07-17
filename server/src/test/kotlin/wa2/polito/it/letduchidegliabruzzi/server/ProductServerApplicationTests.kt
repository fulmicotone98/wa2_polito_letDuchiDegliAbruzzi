package wa2.polito.it.letduchidegliabruzzi.server

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import wa2.polito.it.letduchidegliabruzzi.server.controller.body.*
import wa2.polito.it.letduchidegliabruzzi.server.dal.authDao.UserService
import wa2.polito.it.letduchidegliabruzzi.server.dal.dao.product.ProductService

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductControllerTests {

    companion object {
        @Container
        val postgres = PostgreSQLContainer<Nothing>("postgres:latest")

        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgres::getJdbcUrl)
            registry.add("spring.datasource.username", postgres::getUsername)
            registry.add("spring.datasource.password", postgres::getPassword)
            registry.add("spring.jpa.hibernate.ddl-auto") { "create-drop" }
        }
    }

    @LocalServerPort
    protected var port: Int = 0

    @Autowired
    lateinit var restTemplate: TestRestTemplate

    @Autowired
    lateinit var userService: UserService

    @Autowired
    lateinit var productService: ProductService

    lateinit var httpEntity: HttpEntity<*>

    @Test
    fun `getAll should return a list of products`() {
        // Arrange
        val credentials = CredentialsLogin("manager", "manager")
        val jwtToken = restTemplate.postForEntity(
            "/API/login",
            credentials,
            JwtResponse::class.java
        ).body?.access_token ?: ""
        val headers = HttpHeaders()
        headers.setBearerAuth(jwtToken)
        httpEntity = HttpEntity(null, headers)

        // Act
        val responseEntity = restTemplate.exchange(
            "/API/products",
            HttpMethod.GET,
            httpEntity,
            object : ParameterizedTypeReference<List<ProductResponseBody>>() {}
        )

        // Assert
        Assertions.assertEquals(HttpStatus.OK, responseEntity.statusCode)
        Assertions.assertNotNull(responseEntity.body)
    }

    @Test
    fun `addProduct should create a new product with valid input`() {
        // Arrange
        val credentials = CredentialsLogin("manager", "manager")
        val jwtToken = restTemplate.postForEntity(
            "/API/login",
            credentials,
            JwtResponse::class.java
        ).body?.access_token ?: ""
        val headers = HttpHeaders()
        headers.setBearerAuth(jwtToken)

        val productRequestBody = ProductRequestBody("1234567899", "Brand", "Product Name")

        httpEntity = HttpEntity(productRequestBody, headers)

        // Act
        val responseEntity = restTemplate.exchange(
            "/API/products",
            HttpMethod.POST,
            httpEntity,
            ProductBodyID::class.java
        )

        // Assert
        Assertions.assertEquals(HttpStatus.CREATED, responseEntity.statusCode)
        Assertions.assertNotNull(responseEntity.body?.ean)
    }

    @Test
    fun `addProduct should return HTTP 400 for invalid input`() {
        // Arrange
        val credentials = CredentialsLogin("manager", "manager")
        val jwtToken = restTemplate.postForEntity(
            "/API/login",
            credentials,
            JwtResponse::class.java
        ).body?.access_token ?: ""
        val headers = HttpHeaders()
        headers.setBearerAuth(jwtToken)

        // Empty productRequestBody with invalid input
        val productRequestBody = ProductRequestBody("", "", "")

        httpEntity = HttpEntity(productRequestBody, headers)

        // Act
        val responseEntity = restTemplate.exchange(
            "/API/products",
            HttpMethod.POST,
            httpEntity,
            String::class.java
        )

        // Assert
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.statusCode)
    }
    @Test
    fun `getAllByUser should return a list of products for a valid user`() {
        // Arrange
        val credentials = CredentialsLogin("manager", "manager")
        val jwtToken = restTemplate.postForEntity(
            "/API/login",
            credentials,
            JwtResponse::class.java
        ).body?.access_token ?: ""
        val headers = HttpHeaders()
        headers.setBearerAuth(jwtToken)
        httpEntity = HttpEntity(null, headers)

        // Create a new customer with a unique username
        val customer = UserBody("prova", "prova@example.com", "password", "John", "Doe", "1234567890", "123 Main St")
        userService.addUser(customer, listOf("Customers_group"))

        // Create a new product for the customer
        val productRequestBody = ProductRequestBody("1286878558", "Brand", "Product Name")
        httpEntity = HttpEntity(productRequestBody, headers)
        restTemplate.exchange("/API/products", HttpMethod.POST, httpEntity, ProductBodyID::class.java)

        // Act
        val responseEntity = restTemplate.exchange(
            "/API/products/user",
            HttpMethod.GET,
            httpEntity,
            object : ParameterizedTypeReference<List<ProductResponseBody>>() {}
        )

        // Assert
        Assertions.assertEquals(HttpStatus.OK, responseEntity.statusCode)
        Assertions.assertNotNull(responseEntity.body)
        Assertions.assertTrue(responseEntity.body!!.isNotEmpty())
        Assertions.assertEquals("1234567890", responseEntity.body!!.first().ean)

        userService.deleteUserByUsername("prova")
    }

    @Test
    fun `getProduct should return the product for a valid EAN`() {
        // Arrange
        val credentials = CredentialsLogin("manager", "manager")
        val jwtToken = restTemplate.postForEntity(
            "/API/login",
            credentials,
            JwtResponse::class.java
        ).body?.access_token ?: ""
        val headers = HttpHeaders()
        headers.setBearerAuth(jwtToken)
        httpEntity = HttpEntity(null, headers)

        // Create a new product for testing
        val productRequestBody = ProductRequestBody("1234567890", "Brand", "Product Name")
        httpEntity = HttpEntity(productRequestBody, headers)
        restTemplate.exchange("/API/products", HttpMethod.POST, httpEntity, ProductBodyID::class.java)

        // Act
        val responseEntity = restTemplate.exchange(
            "/API/products/1234567890",
            HttpMethod.GET,
            httpEntity,
            ProductResponseBody::class.java
        )

        // Assert
        Assertions.assertEquals(HttpStatus.OK, responseEntity.statusCode)
        Assertions.assertNotNull(responseEntity.body)
        Assertions.assertEquals("1234567890", responseEntity.body?.ean)

    }

    @Test
    fun `getProduct should return HTTP 404 for a non-existent EAN`() {
        // Arrange
        val credentials = CredentialsLogin("manager", "manager")
        val jwtToken = restTemplate.postForEntity(
            "/API/login",
            credentials,
            JwtResponse::class.java
        ).body?.access_token ?: ""
        val headers = HttpHeaders()
        headers.setBearerAuth(jwtToken)
        httpEntity = HttpEntity(null, headers)

        // Act
        val responseEntity = restTemplate.exchange(
            "/API/products/11111111111111111111",
            HttpMethod.GET,
            httpEntity,
            String::class.java
        )

        // Assert
        Assertions.assertEquals(HttpStatus.NOT_FOUND, responseEntity.statusCode)
        Assertions.assertTrue(responseEntity.body?.contains("Product not found") ?: false)
    }

}
