package wa2.polito.it.letduchidegliabruzzi.server

import junit.framework.TestCase.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import wa2.polito.it.letduchidegliabruzzi.server.customer.*
import wa2.polito.it.letduchidegliabruzzi.server.employee.*
import wa2.polito.it.letduchidegliabruzzi.server.employee.BodyObject
import wa2.polito.it.letduchidegliabruzzi.server.product.*
import wa2.polito.it.letduchidegliabruzzi.server.ticket.*

@Testcontainers
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace=AutoConfigureTestDatabase.Replace.NONE)
class CustomerServerApplicationTests {
    companion object {
        @Container
        val postgres = PostgreSQLContainer("postgres:latest")
        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgres::getJdbcUrl)
            registry.add("spring.datasource.username", postgres::getUsername)
            registry.add("spring.datasource.password", postgres::getPassword)
            registry.add("spring.jpa.hibernate.ddl-auto") {"create-drop"}
        }
    }
    @LocalServerPort
    protected var port: Int = 0
    @Autowired
    lateinit var restTemplate: TestRestTemplate
    @Autowired
    lateinit var customerRepository: CustomerRepository
    @Autowired
    lateinit var customerService: CustomerService
    @Autowired
    lateinit var productService: ProductService
    @Autowired
    lateinit var ticketService: TicketService
    @Test
    fun `getProfile should return the customer profile for a valid email`() {
        // Create a new customer with a unique email
        val customer = Customer("johndoe@example.com","John", "Doe", "1234567890", "123 Main St")
        customerRepository.save(customer)

        // Make a GET request to the getProfile endpoint with the customer's email
        val responseEntity = restTemplate.getForEntity("/API/profiles/${customer.email}", CustomerResponseBody::class.java)

        // Assert that the response has HTTP status 200 (OK)
        assertEquals(HttpStatus.OK, responseEntity.statusCode)

        // Assert that the response body is not null
        assertNotNull(responseEntity.body)

        // Assert that the response body fields match the customer's data
        assertEquals(customer.email, responseEntity.body?.email)
        assertEquals(customer.name, responseEntity.body?.name)
        assertEquals(customer.surname, responseEntity.body?.surname)
        assertEquals(customer.phonenumber, responseEntity.body?.phonenumber)
        assertEquals(customer.address, responseEntity.body?.address)
    }

    @Test
    fun `getProfile should return HTTP 404 for a non-existent email`() {
        // Make a GET request to the getProfile endpoint with a non-existent email
        val email = "nonexistent@example.com"
        val responseEntity = restTemplate.getForEntity("/API/profiles/$email", String::class.java)

        // Assert that the response has HTTP status 404 (NOT FOUND)
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.statusCode)

        // Assert that the response body contains the expected error message
        val expectedErrorMessage = "Customer not found with Email: $email"
        assertTrue(responseEntity.body?.contains(expectedErrorMessage) ?: false)
    }

    @Test
    fun `getProfile should return HTTP 400 for an invalid email`() {
        // Make a GET request to the getProfile endpoint with an invalid email
        val invalidEmail = "notanemail"
        val responseEntity = restTemplate.getForEntity("/API/profiles/$invalidEmail", String::class.java)

        // Assert that the response has HTTP status 400 (BAD REQUEST)
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.statusCode)

        // Assert that the response body contains the expected error message
        val expectedErrorMessage = "Not an email"
        println(responseEntity.body)
        assertTrue(responseEntity.body?.contains(expectedErrorMessage) ?: false)
    }
    @Test
    fun `getCustomerTickets should return the customer's tickets for a valid email`() {
        // Create a new customer with a unique email
        val email = "test@example.com"
        // Create a mock customer and tickets associated with the email
        val customer = CustomerDTO("Test", "Customer", "123456789", "123 Test Street", email)
        customerService.addProfile(customer)
        val product1 = productService.addProduct("1234567890123", "Test Brand 1", "Test Product 1", email)
        val product2 = productService.addProduct("1234567890124", "Test Brand 2", "Test Product 2", email)
        val savedTicket1 = ticketService.addTicket("Ticket test1", product1.ean, email)
        val savedTicket2 = ticketService.addTicket("Ticket test2", product2.ean, email)

        // Make a GET request to the getProfile endpoint with the customer's email
        val response = restTemplate.exchange("/API/profile/${email}/tickets", HttpMethod.GET, null, object : ParameterizedTypeReference<List<TicketResponseBody>>() {})
        val responseBody = response.body!!
        // Assert that the response has HTTP status 200 (OK)
        assertEquals(HttpStatus.OK, response.statusCode)

        // Assert that the response body is not null
        assertNotNull(responseBody)
        assertEquals(2, responseBody.size)
        assertNotNull(responseBody[0].ticketID)
        assertNotNull(responseBody[1].ticketID)
        // Assert that the response body fields match the customer's data
        assertEquals(savedTicket1.description, responseBody[0].description)
        assertEquals(savedTicket2.description, responseBody[1].description)
        assertEquals(savedTicket1.status, responseBody[0].status)
        assertEquals(savedTicket2.status, responseBody[1].status)
        assertNotNull(responseBody[0].createdAt)
        assertNotNull(responseBody[1].createdAt)
    }

    @Test
    fun `getCustomerTickets should return HTTP 404 for a non-existent email`() {
        // Make a GET request to the getProfile endpoint with a non-existent email
        val email = "nonexistent@example.com"
        val responseEntity = restTemplate.getForEntity("/API/profile/$email/tickets", String::class.java)

        // Assert that the response has HTTP status 404 (NOT FOUND)
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.statusCode)

        // Assert that the response body contains the expected error message
        val expectedErrorMessage = "Customer not found with Email: $email"
        assertTrue(responseEntity.body?.contains(expectedErrorMessage) ?: false)
    }

    @Test
    fun `getCustomerTickets should return HTTP 400 for an invalid email`() {
        // Make a GET request to the getProfile endpoint with an invalid email
        val invalidEmail = "notanemail"
        val responseEntity = restTemplate.getForEntity("/API/profile/$invalidEmail/tickets", String::class.java)

        // Assert that the response has HTTP status 400 (BAD REQUEST)
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.statusCode)

        // Assert that the response body contains the expected error message
        val expectedErrorMessage = "Not an email"
        println(responseEntity.body)
        assertTrue(responseEntity.body?.contains(expectedErrorMessage) ?: false)
    }
    @Test
    fun `addProfile should add a new customer profile`() {
        // Create a new customer request body with valid data
        val requestBody = CustomerRequestBody("mariorossi@example.com","Mario", "Rossi", "123 Main St","1234567890")

        // Make a POST request to the addProfile endpoint with the request body
        val responseEntity = restTemplate.postForEntity("/API/profiles", requestBody, CustomerResponseBody::class.java)

        // Assert that the response has HTTP status 201 (CREATED)
        assertEquals(HttpStatus.CREATED, responseEntity.statusCode)

        // Assert that the response body is not null
        assertNotNull(responseEntity.body)

        // Assert that the response body email field matches the request body email field
        assertEquals(requestBody.email, responseEntity.body?.email)

        // Assert that the response body other fields are null (as expected)
        assertNull(responseEntity.body?.name)
        assertNull(responseEntity.body?.surname)
        assertNull(responseEntity.body?.phonenumber)
        assertNull(responseEntity.body?.address)

        // Assert that the customer was added to the database by checking if it can be retrieved
        val customer = customerService.getProfile(requestBody.email)
        assertNotNull(customer)
        assertEquals(requestBody.name, customer?.name)
        assertEquals(requestBody.surname, customer?.surname)
        assertEquals(requestBody.phonenumber, customer?.phonenumber)
        assertEquals(requestBody.address, customer?.address)
    }

    @Test
    fun `addProfile should return 400 error for invalid input`() {
        // Create a new customer request body with valid data
        val requestBody = CustomerRequestBody("abc","John", "Doe", "123 Main St","1234567890")

        // Make a POST request to the addProfile endpoint with the request body
        val responseEntity = restTemplate.postForEntity("/API/profiles", requestBody, String::class.java)

        // Assert that the response has HTTP status 201 (CREATED)
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.statusCode)
        val expectedErrorMessage = "The email should be provided in a correct format"
        assertTrue(responseEntity.body?.contains(expectedErrorMessage) ?: false)
    }

    @Test
    fun `addProfile should return 409 error for duplicate insertion`() {
        // Create a new customer request body with valid data
        val requestBody = CustomerRequestBody("pincopallino@example.com","Pinco", "Pallino", "123 Main St","1234567890")

        // Make a POST request to the addProfile endpoint with the request body
        restTemplate.postForEntity("/API/profiles", requestBody, String::class.java)
        val responseEntity = restTemplate.postForEntity("/API/profiles", requestBody, String::class.java)
        // Assert that the response has HTTP status 201 (CREATED)
        assertEquals(HttpStatus.CONFLICT, responseEntity.statusCode)
        val expectedErrorMessage = "Customer already exists"
        assertTrue(responseEntity.body?.contains(expectedErrorMessage) ?: false)
    }

    @Test
    fun `updateProfile should update an existing customer profile`() {
        // Create a new customer request body with valid data
        val customer = Customer("johndoe@example.com","John", "Doe", "123 Main St","1234567890")
        customerRepository.save(customer)
        val requestBody = CustomerRequestBody("johndoe@example.com","Mario", "Rossi", "2 Second St","1234567893")
        // Make a PUT request to the updateProfile endpoint with the request body
        val responseEntity = restTemplate.exchange("/API/profiles/${customer.email}", HttpMethod.PUT, HttpEntity(requestBody), String::class.java)
        println(responseEntity)
        // Assert that the response has HTTP status 204 (NO_CONTENT)
        assertEquals(HttpStatus.NO_CONTENT, responseEntity.statusCode)

        // Assert that the customer was added to the database by checking if it can be retrieved
        val newCustomer = customerService.getProfile(requestBody.email)
        assertNotNull(customer)
        assertEquals(requestBody.name, newCustomer?.name)
        assertEquals(requestBody.surname, newCustomer?.surname)
        assertEquals(requestBody.phonenumber, newCustomer?.phonenumber)
        assertEquals(requestBody.address, newCustomer?.address)
    }

    @Test
    fun `updateProfile should return 400 error for invalid input`() {
        // Create a new customer request body with valid data
        val customer = Customer("johndoe@example.com","John", "Doe", "123 Main St","1234567890")
        customerRepository.save(customer)
        val requestBody = CustomerRequestBody("johndoe@example.com","", "Rossi", "2 Second St","1234567893")
        // Make a PUT request to the updateProfile endpoint with the request body
        val responseEntity = restTemplate.exchange("/API/profiles/${customer.email}", HttpMethod.PUT, HttpEntity(requestBody), String::class.java)
        println(responseEntity)
        // Assert that the response has HTTP status 204 (NO_CONTENT)
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.statusCode)

        // Assert that the response body contains the expected error message
        val expectedErrorMessage = "The name should not be blank"
        println(responseEntity.body)
        assertTrue(responseEntity.body?.contains(expectedErrorMessage) ?: false)
    }

    @Test
    fun `updateProfile should return HTTP 404 for a non-existent email`() {
        // Make a GET request to the getProfile endpoint with a non-existent email
        val email = "nonexistent@example.com"
        val requestBody = CustomerRequestBody("johndoe@example.com","Mario", "Rossi", "2 Second St","1234567893")

        val responseEntity = restTemplate.exchange("/API/profiles/${email}", HttpMethod.PUT, HttpEntity(requestBody), String::class.java)

        // Assert that the response has HTTP status 404 (NOT FOUND)
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.statusCode)

        // Assert that the response body contains the expected error message
        val expectedErrorMessage = "Customer not found with Email: $email"
        assertTrue(responseEntity.body?.contains(expectedErrorMessage) ?: false)
    }
}
@Testcontainers
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace=AutoConfigureTestDatabase.Replace.NONE)
class EmployeeServerApplicationTests {
    companion object {
        @Container
        val postgres = PostgreSQLContainer("postgres:latest")
        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgres::getJdbcUrl)
            registry.add("spring.datasource.username", postgres::getUsername)
            registry.add("spring.datasource.password", postgres::getPassword)
            registry.add("spring.jpa.hibernate.ddl-auto") {"create-drop"}
        }
    }
    @LocalServerPort
    protected var port: Int = 0
    @Autowired
    lateinit var restTemplate: TestRestTemplate
    @Autowired
    lateinit var employeeRepository: EmployeeRepository
    @Autowired
    lateinit var employeeService: EmployeeService
    @Test
    fun `getEmployee should return the employee for a valid id`() {
        // Create a new customer with a unique email
        val employee = Employee(1,"johndoe@test.it","John","Doe","expert")
        employeeRepository.save(employee)
        // Make a GET request to the getProfile endpoint with the customer's email
        val responseEntity = restTemplate.getForEntity("/API/employees/${employee.employeeID}", EmployeeResponseBody::class.java)

        // Assert that the response has HTTP status 200 (OK)
        assertEquals(HttpStatus.OK, responseEntity.statusCode)

        // Assert that the response body is not null
        assertNotNull(responseEntity.body)
        // Assert that the response body fields match the customer's data
        assertEquals("johndoe@test.it", responseEntity.body?.email)
        assertEquals("John", responseEntity.body?.name)
        assertEquals("Doe", responseEntity.body?.surname)
        assertEquals("expert", responseEntity.body?.role)
    }

    @Test
    fun `getEmployee should return HTTP 404 for a non-existent id`() {
        // Make a GET request to the getProfile endpoint with a non-existent email
        val id = 54152
        val responseEntity = restTemplate.getForEntity("/API/employees/$id", String::class.java)

        // Assert that the response has HTTP status 404 (NOT FOUND)
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.statusCode)

        // Assert that the response body contains the expected error message
        val expectedErrorMessage = "Employee not found"
        assertTrue(responseEntity.body?.contains(expectedErrorMessage) ?: false)
    }

    @Test
    fun `getEmployee should return HTTP 400 for an invalid id`() {
        // Make a GET request to the getProfile endpoint with an invalid email
        val invalidId = "notanid"
        val responseEntity = restTemplate.getForEntity("/API/employees/$invalidId", String::class.java)

        // Assert that the response has HTTP status 400 (BAD REQUEST)
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.statusCode)

        // Assert that the response body contains the expected error message
        val expectedErrorMessage = "Failed to convert 'id' with value: '$invalidId'"
        println(responseEntity.body)
        assertTrue(responseEntity.body?.contains(expectedErrorMessage) ?: false)
    }

    @Test
    fun `addEmployee should add a new employee`() {
        // Create a new customer request body with valid data
        val requestBody = BodyObject("mariorossi@example.com","Mario", "expert", "Rossi")

        // Make a POST request to the addProfile endpoint with the request body
        val responseEntity = restTemplate.postForEntity("/API/employee", requestBody, EmployeeResponseBody::class.java)

        // Assert that the response has HTTP status 201 (CREATED)
        assertEquals(HttpStatus.CREATED, responseEntity.statusCode)

        // Assert that the response body is not null
        assertNotNull(responseEntity.body)

        // Assert that the response body email field matches the request body email field
        assertTrue(responseEntity.body?.employeeID!! >0)
        println(responseEntity.body)
        assertNull(responseEntity.body?.name)
        assertNull(responseEntity.body?.surname)
        assertNull(responseEntity.body?.email)
        assertNull(responseEntity.body?.role)


        // Assert that the customer was added to the database by checking if it can be retrieved
        val employee = employeeService.getEmployeeByID(responseEntity.body!!.employeeID)
        assertNotNull(employee)
        assertEquals(requestBody.name, employee?.name)
        assertEquals(requestBody.surname, employee?.surname)
        assertEquals(requestBody.email, employee?.email)
        assertEquals(requestBody.role, employee?.role)
    }

    @Test
    fun `addEmployee should return 400 error for invalid input`() {
        // Create a new customer request body with valid data
        val requestBody = BodyObject("abc","John", "expert", "Doe")

        // Make a POST request to the addProfile endpoint with the request body
        val responseEntity = restTemplate.postForEntity("/API/employee", requestBody, String::class.java)
        println(responseEntity)
        // Assert that the response has HTTP status 201 (CREATED)
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.statusCode)
        val expectedErrorMessage = "The email should be provided in a correct format"
        assertTrue(responseEntity.body?.contains(expectedErrorMessage) ?: false)
    }

    @Test
    fun `addEmployee should return 400 error for invalid role`() {
        // Create a new customer request body with valid data
        val requestBody = BodyObject("test@gmail.com","John", "test", "Doe")

        // Make a POST request to the addProfile endpoint with the request body
        val responseEntity = restTemplate.postForEntity("/API/employee", requestBody, String::class.java)
        println(responseEntity)
        // Assert that the response has HTTP status 201 (CREATED)
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.statusCode)
        val expectedErrorMessage = "Role must be expert or manager"
        assertTrue(responseEntity.body?.contains(expectedErrorMessage) ?: false)
    }
}

@Testcontainers
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace=AutoConfigureTestDatabase.Replace.NONE)
class ProductsServerApplicationTests {
    companion object {
        @Container
        val postgres = PostgreSQLContainer("postgres:latest")
        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgres::getJdbcUrl)
            registry.add("spring.datasource.username", postgres::getUsername)
            registry.add("spring.datasource.password", postgres::getPassword)
            registry.add("spring.jpa.hibernate.ddl-auto") {"create-drop"}
        }
    }
    @LocalServerPort
    protected var port: Int = 0
    @Autowired
    lateinit var restTemplate: TestRestTemplate
    @Autowired
    lateinit var productRepository: ProductRepository
    @Autowired
    lateinit var customerRepository: CustomerRepository
    @Autowired
    lateinit var productService: ProductService

    @Test
    fun `test getAll method should return all products`() {
        val customer = Customer("johndoe@example.com","John", "Doe", "1234567890", "123 Main St")
        customerRepository.save(customer)
        // Create some test data
        productService.addProduct("1234567890123", "Test Brand 1", "Test Product 1", "johndoe@example.com")
        productService.addProduct("2345678901234", "Test Brand 2", "Test Product 2", "johndoe@example.com")

        // Make a GET request to the /API/products endpoint
        val response = restTemplate.exchange("/API/products", HttpMethod.GET, null, object : ParameterizedTypeReference<List<ProductResponseBody>>() {})

        // Verify that the response status is OK
        assertEquals(HttpStatus.OK, response.statusCode)

        // Verify that the response body contains the expected data
        val responseBody = response.body!!
        assertEquals(2, responseBody.size)
        assertEquals("1234567890123", responseBody[0].ean)
        assertEquals("Test Product 1", responseBody[0].name)
        assertEquals("Test Brand 1", responseBody[0].brand)
        assertEquals("johndoe@example.com", responseBody[0].customerEmail)
        assertEquals("2345678901234", responseBody[1].ean)
        assertEquals("Test Product 2", responseBody[1].name)
        assertEquals("Test Brand 2", responseBody[1].brand)
        assertEquals("johndoe@example.com", responseBody[1].customerEmail)
    }
    @Test
    fun `getProduct should return the product for a valid ean`() {
        // Create a new customer with a unique email
        val customer = Customer("johndoe@example.com","John", "Doe", "1234567890", "123 Main St")
        customerRepository.save(customer)
        // Create some test data
        productService.addProduct("1234567890123", "Test Brand 1", "Test Product 1", "johndoe@example.com")

        // Make a GET request to the getProfile endpoint with the customer's email
        val responseEntity = restTemplate.getForEntity("/API/products/1234567890123", ProductResponseBody::class.java)

        // Assert that the response has HTTP status 200 (OK)
        assertEquals(HttpStatus.OK, responseEntity.statusCode)

        // Assert that the response body is not null
        assertNotNull(responseEntity.body)

        // Assert that the response body fields match the customer's data
        assertEquals("1234567890123", responseEntity.body?.ean)
        assertEquals("Test Brand 1", responseEntity.body?.brand)
        assertEquals("Test Product 1", responseEntity.body?.name)
        assertEquals("johndoe@example.com", responseEntity.body?.customerEmail)
    }

    @Test
    fun `getProduct should return HTTP 404 for a non-existent product`() {
        // Make a GET request to the getProfile endpoint with a non-existent email
        val ean = "11111111111"
        val responseEntity = restTemplate.getForEntity("/API/products/$ean", String::class.java)

        // Assert that the response has HTTP status 404 (NOT FOUND)
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.statusCode)

        // Assert that the response body contains the expected error message
        val expectedErrorMessage = "Product not found"
        assertTrue(responseEntity.body?.contains(expectedErrorMessage) ?: false)
    }

    @Test
    fun `getProduct should return HTTP 400 for an invalid ean`() {
        // Make a GET request to the getProfile endpoint with an invalid email
        val invalidEan = "%&$"
        val responseEntity = restTemplate.getForEntity("/API/products/$invalidEan", String::class.java)

        // Assert that the response has HTTP status 400 (BAD REQUEST)
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.statusCode)

        // Assert that the response body contains the expected error message
        val expectedErrorMessage = "The Ean should be alphanumeric"
        println(responseEntity.body)
        assertTrue(responseEntity.body?.contains(expectedErrorMessage) ?: false)
    }

    @Test
    fun `addProduct should add a new product`() {
        val customer = Customer("johndoe@example.com","John", "Doe", "1234567890", "123 Main St")
        customerRepository.save(customer)
        // Create a new customer request body with valid data
        val requestBody = ProductRequestBody("1234567890123", "Test Brand 1", "Test Product 1", "johndoe@example.com")

        // Make a POST request to the addProfile endpoint with the request body
        val responseEntity = restTemplate.postForEntity("/API/products", requestBody, ProductResponseBody::class.java)

        // Assert that the response has HTTP status 201 (CREATED)
        assertEquals(HttpStatus.CREATED, responseEntity.statusCode)

        // Assert that the response body is not null
        assertNotNull(responseEntity.body)

        // Assert that the response body email field matches the request body email field
        assertEquals(requestBody.ean, responseEntity.body?.ean)
        println(responseEntity.body)
        // Assert that the response body other fields are null (as expected)
        assertNull(responseEntity.body?.name)
        assertNull(responseEntity.body?.customerEmail)
        assertNull(responseEntity.body?.brand)

        // Assert that the customer was added to the database by checking if it can be retrieved
        val product = productService.getProduct(requestBody.ean)
        assertNotNull(customer)
        assertEquals(requestBody.name, product?.name)
        assertEquals(requestBody.brand, product?.brand)
        assertEquals(requestBody.customerEmail, product?.customer!!.email)
    }
    @Test
    fun `addProduct should return 400 error for invalid input`() {
        // Create a new customer request body with valid data
        val requestBody = ProductRequestBody("£$%", "Test Brand 1", "Test Product 1", "johndoe")

        // Make a POST request to the addProfile endpoint with the request body
        val responseEntity = restTemplate.postForEntity("/API/products", requestBody, String::class.java)

        // Assert that the response has HTTP status 201 (CREATED)
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.statusCode)
        val expectedErrorMessage = "The email should be provided in a correct format"
        assertTrue(responseEntity.body?.contains(expectedErrorMessage) ?: false)
    }

    @Test
    fun `addProduct should return 404 error for customer not found`() {
        // Create a new customer request body with valid data
        val requestBody = ProductRequestBody("123abc", "Test Brand 1", "Test Product 1", "johndoe@abc.it")

        // Make a POST request to the addProfile endpoint with the request body
        val responseEntity = restTemplate.postForEntity("/API/products", requestBody, String::class.java)

        // Assert that the response has HTTP status 201 (CREATED)
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.statusCode)
        val expectedErrorMessage = "Customer not found with Email: johndoe@abc.it"
        assertTrue(responseEntity.body?.contains(expectedErrorMessage) ?: false)
    }
}