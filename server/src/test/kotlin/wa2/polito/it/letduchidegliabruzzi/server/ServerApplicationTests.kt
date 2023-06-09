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
import wa2.polito.it.letduchidegliabruzzi.server.entity.customer.*
import wa2.polito.it.letduchidegliabruzzi.server.entity.employee.*
import wa2.polito.it.letduchidegliabruzzi.server.entity.product.ProductService
import wa2.polito.it.letduchidegliabruzzi.server.entity.ticket.*
import wa2.polito.it.letduchidegliabruzzi.server.security.CredentialsLogin
import wa2.polito.it.letduchidegliabruzzi.server.security.JwtResponse

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

    lateinit var httpEntity: HttpEntity<*>

    @Test
    fun `getProfile should return the customer profile for a valid email`() {

        val credentials = CredentialsLogin("manager", "manager")
        val jwtToken = restTemplate
            .postForEntity("/API/login", credentials, JwtResponse::class.java).body?.jwt ?: ""
        val headers = HttpHeaders()
        headers.setBearerAuth(jwtToken)
        httpEntity = HttpEntity(null, headers)

        // Create a new customer with a unique email
        val customer = Customer("johndoe@example.com","John", "Doe", "1234567890", "123 Main St")
        customerRepository.save(customer)

        // Make a GET request to the getProfile endpoint with the customer's email
        val responseEntity = restTemplate.exchange(
            "/API/profiles/${customer.email}",
            HttpMethod.GET,
            httpEntity,
            CustomerResponseBody::class.java
        )

        // Assert that the response has HTTP status 200 (OK)
        Assertions.assertEquals(HttpStatus.OK, responseEntity.statusCode)

        // Assert that the response body is not null
        Assertions.assertNotNull(responseEntity.body)

        // Assert that the response body fields match the customer's data
        Assertions.assertEquals(customer.email, responseEntity.body?.email)
        Assertions.assertEquals(customer.name, responseEntity.body?.name)
        Assertions.assertEquals(customer.surname, responseEntity.body?.surname)
        Assertions.assertEquals(customer.phonenumber, responseEntity.body?.phonenumber)
        Assertions.assertEquals(customer.address, responseEntity.body?.address)
    }

    @Test
    fun `getProfile should return HTTP 404 for a non-existent email`() {

        val credentials = CredentialsLogin("manager", "manager")
        val jwtToken = restTemplate
            .postForEntity("/API/login", credentials, JwtResponse::class.java).body?.jwt ?: ""
        val headers = HttpHeaders()
        headers.setBearerAuth(jwtToken)
        httpEntity = HttpEntity(null, headers)

        // Make a GET request to the getProfile endpoint with a non-existent email
        val email = "nonexistent@example.com"
        val responseEntity = restTemplate.exchange("/API/profiles/$email", HttpMethod.GET, httpEntity, String::class.java)

        // Assert that the response has HTTP status 404 (NOT FOUND)
        Assertions.assertEquals(HttpStatus.NOT_FOUND, responseEntity.statusCode)

        // Assert that the response body contains the expected error message
        val expectedErrorMessage = "Customer not found with Email: $email"
        Assertions.assertTrue(responseEntity.body?.contains(expectedErrorMessage) ?: false)
    }

    @Test
    fun `getProfile should return HTTP 400 for an invalid email`() {
        // Make a GET request to the getProfile endpoint with an invalid email
        val invalidEmail = "notanemail"
        val credentials = CredentialsLogin("manager", "manager")
        val jwtToken = restTemplate
            .postForEntity("/API/login", credentials, JwtResponse::class.java).body?.jwt ?: ""
        val headers = HttpHeaders()
        headers.setBearerAuth(jwtToken)
        httpEntity = HttpEntity(null, headers)
        val responseEntity = restTemplate.exchange("/API/profiles/$invalidEmail", HttpMethod.GET, httpEntity, String::class.java)

        // Assert that the response has HTTP status 400 (BAD REQUEST)
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.statusCode)

        // Assert that the response body contains the expected error message
        val expectedErrorMessage = "Not an email"
        println(responseEntity.body)
        Assertions.assertTrue(responseEntity.body?.contains(expectedErrorMessage) ?: false)
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
        val credentials = CredentialsLogin("manager", "manager")
        val jwtToken = restTemplate
            .postForEntity("/API/login", credentials, JwtResponse::class.java).body?.jwt ?: ""
        val headers = HttpHeaders()
        headers.setBearerAuth(jwtToken)
        httpEntity = HttpEntity(null, headers)
        // Make a GET request to the getProfile endpoint with the customer's email
        val response = restTemplate.exchange("/API/profile/${email}/tickets", HttpMethod.GET, httpEntity, object : ParameterizedTypeReference<List<TicketBodyResponse>>() {})
        val responseBody = response.body!!
        // Assert that the response has HTTP status 200 (OK)
        Assertions.assertEquals(HttpStatus.OK, response.statusCode)

        // Assert that the response body is not null
        Assertions.assertNotNull(responseBody)
        Assertions.assertEquals(2, responseBody.size)
        Assertions.assertNotNull(responseBody[0].ticketID)
        Assertions.assertNotNull(responseBody[1].ticketID)
        // Assert that the response body fields match the customer's data
        Assertions.assertEquals(savedTicket1.description, responseBody[0].description)
        Assertions.assertEquals(savedTicket2.description, responseBody[1].description)
        Assertions.assertEquals(savedTicket1.status, responseBody[0].status)
        Assertions.assertEquals(savedTicket2.status, responseBody[1].status)
        Assertions.assertNotNull(responseBody[0].createdAt)
        Assertions.assertNotNull(responseBody[1].createdAt)
    }

    @Test
    fun `getCustomerTickets should return HTTP 404 for a non-existent email`() {
        // Make a GET request to the getProfile endpoint with a non-existent email
        val email = "nonexistent@example.com"
        val credentials = CredentialsLogin("manager", "manager")
        val jwtToken = restTemplate
            .postForEntity("/API/login", credentials, JwtResponse::class.java).body?.jwt ?: ""
        val headers = HttpHeaders()
        headers.setBearerAuth(jwtToken)
        httpEntity = HttpEntity(null, headers)

        val responseEntity = restTemplate.exchange("/API/profile/$email/tickets", HttpMethod.GET, httpEntity, String::class.java)

        // Assert that the response has HTTP status 404 (NOT FOUND)
        Assertions.assertEquals(HttpStatus.NOT_FOUND, responseEntity.statusCode)

        // Assert that the response body contains the expected error message
        val expectedErrorMessage = "Customer not found with Email: $email"
        Assertions.assertTrue(responseEntity.body?.contains(expectedErrorMessage) ?: false)
    }

    @Test
    fun `getCustomerTickets should return HTTP 400 for an invalid email`() {
        // Make a GET request to the getProfile endpoint with an invalid email
        val invalidEmail = "notanemail"
        val credentials = CredentialsLogin("manager", "manager")
        val jwtToken = restTemplate
            .postForEntity("/API/login", credentials, JwtResponse::class.java).body?.jwt ?: ""
        val headers = HttpHeaders()
        headers.setBearerAuth(jwtToken)
        httpEntity = HttpEntity(null, headers)

        val responseEntity = restTemplate.exchange("/API/profile/$invalidEmail/tickets", HttpMethod.GET, httpEntity, String::class.java)

        // Assert that the response has HTTP status 400 (BAD REQUEST)
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.statusCode)

        // Assert that the response body contains the expected error message
        val expectedErrorMessage = "Not an email"
        println(responseEntity.body)
        Assertions.assertTrue(responseEntity.body?.contains(expectedErrorMessage) ?: false)
    }
    @Test
    fun `addProfile should add a new customer profile`() {
        // Create a new customer request body with valid data
        val requestBody = CustomerRequestBody("mariorossi@example.com","Mario", "Rossi", "123 Main St","1234567890")
        val credentials = CredentialsLogin("manager", "manager")
        val jwtToken = restTemplate
            .postForEntity("/API/login", credentials, JwtResponse::class.java).body?.jwt ?: ""
        val headers = HttpHeaders()
        headers.setBearerAuth(jwtToken)
        httpEntity = HttpEntity(requestBody, headers)

        // Make a POST request to the addProfile endpoint with the request body
        val responseEntity = restTemplate.exchange("/API/profiles", HttpMethod.POST, httpEntity, CustomerResponseBody::class.java)

        // Assert that the response has HTTP status 201 (CREATED)
        Assertions.assertEquals(HttpStatus.CREATED, responseEntity.statusCode)

        // Assert that the response body is not null
        Assertions.assertNotNull(responseEntity.body)

        // Assert that the response body email field matches the request body email field
        Assertions.assertEquals(requestBody.email, responseEntity.body?.email)

        // Assert that the response body other fields are null (as expected)
        Assertions.assertNull(responseEntity.body?.name)
        Assertions.assertNull(responseEntity.body?.surname)
        Assertions.assertNull(responseEntity.body?.phonenumber)
        Assertions.assertNull(responseEntity.body?.address)

        // Assert that the customer was added to the database by checking if it can be retrieved
        val customer = customerService.getProfile(requestBody.email)
        Assertions.assertNotNull(customer)
        Assertions.assertEquals(requestBody.name, customer?.name)
        Assertions.assertEquals(requestBody.surname, customer?.surname)
        Assertions.assertEquals(requestBody.phonenumber, customer?.phonenumber)
        Assertions.assertEquals(requestBody.address, customer?.address)
    }

    @Test
    fun `addProfile should return 400 error for invalid input`() {
        // Create a new customer request body with valid data
        val requestBody = CustomerRequestBody("abc","John", "Doe", "123 Main St","1234567890")
        val credentials = CredentialsLogin("manager", "manager")
        val jwtToken = restTemplate
            .postForEntity("/API/login", credentials, JwtResponse::class.java).body?.jwt ?: ""
        val headers = HttpHeaders()
        headers.setBearerAuth(jwtToken)
        httpEntity = HttpEntity(requestBody, headers)

        // Make a POST request to the addProfile endpoint with the request body
        val responseEntity = restTemplate.exchange("/API/profiles", HttpMethod.POST, httpEntity, String::class.java)

        // Assert that the response has HTTP status 201 (CREATED)
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.statusCode)
        val expectedErrorMessage = "The email should be provided in a correct format"
        Assertions.assertTrue(responseEntity.body?.contains(expectedErrorMessage) ?: false)
    }

    @Test
    fun `addProfile should return 409 error for duplicate insertion`() {
        // Create a new customer request body with valid data
        val requestBody = CustomerRequestBody("pincopallino@example.com","Pinco", "Pallino", "123 Main St","1234567890")
        val credentials = CredentialsLogin("manager", "manager")
        val jwtToken = restTemplate
            .postForEntity("/API/login", credentials, JwtResponse::class.java).body?.jwt ?: ""
        val headers = HttpHeaders()
        headers.setBearerAuth(jwtToken)
        httpEntity = HttpEntity(requestBody, headers)

        // Make a POST request to the addProfile endpoint with the request body
        restTemplate.exchange("/API/profiles", HttpMethod.POST,httpEntity, String::class.java)
        val responseEntity = restTemplate.exchange("/API/profiles", HttpMethod.POST,httpEntity, String::class.java)
        // Assert that the response has HTTP status 201 (CREATED)
        Assertions.assertEquals(HttpStatus.CONFLICT, responseEntity.statusCode)
        val expectedErrorMessage = "Customer already exists"
        Assertions.assertTrue(responseEntity.body?.contains(expectedErrorMessage) ?: false)
    }

    @Test
    fun `updateProfile should update an existing customer profile`() {
        // Create a new customer request body with valid data
        val customer = Customer("johndoe@example.com","John", "Doe", "123 Main St","1234567890")
        customerRepository.save(customer)
        val requestBody = CustomerRequestBody("johndoe@example.com","Mario", "Rossi", "2 Second St","1234567893")

        val credentials = CredentialsLogin("manager", "manager")
        val jwtToken = restTemplate
            .postForEntity("/API/login", credentials, JwtResponse::class.java).body?.jwt ?: ""
        val headers = HttpHeaders()
        headers.setBearerAuth(jwtToken)
        httpEntity = HttpEntity(requestBody, headers)// Make a PUT request to the updateProfile endpoint with the request body
        val responseEntity = restTemplate.exchange("/API/profiles/${customer.email}", HttpMethod.PUT, httpEntity, String::class.java)
        println(responseEntity)
        // Assert that the response has HTTP status 204 (NO_CONTENT)
        Assertions.assertEquals(HttpStatus.NO_CONTENT, responseEntity.statusCode)

        // Assert that the customer was added to the database by checking if it can be retrieved
        val newCustomer = customerService.getProfile(requestBody.email)
        Assertions.assertNotNull(customer)
        Assertions.assertEquals(requestBody.name, newCustomer?.name)
        Assertions.assertEquals(requestBody.surname, newCustomer?.surname)
        Assertions.assertEquals(requestBody.phonenumber, newCustomer?.phonenumber)
        Assertions.assertEquals(requestBody.address, newCustomer?.address)
    }

    @Test
    fun `updateProfile should return 400 error for invalid input`() {
        // Create a new customer request body with valid data
        val customer = Customer("johndoe@example.com","John", "Doe", "123 Main St","1234567890")
        customerRepository.save(customer)
        val requestBody = CustomerRequestBody("johndoe@example.com","", "Rossi", "2 Second St","1234567893")
        // Make a PUT request to the updateProfile endpoint with the request body
        val credentials = CredentialsLogin("manager", "manager")
        val jwtToken = restTemplate
            .postForEntity("/API/login", credentials, JwtResponse::class.java).body?.jwt ?: ""
        val headers = HttpHeaders()
        headers.setBearerAuth(jwtToken)
        httpEntity = HttpEntity(requestBody, headers)
        val responseEntity = restTemplate.exchange("/API/profiles/${customer.email}", HttpMethod.PUT, httpEntity, String::class.java)
        println(responseEntity)
        // Assert that the response has HTTP status 204 (NO_CONTENT)
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.statusCode)

        // Assert that the response body contains the expected error message
        val expectedErrorMessage = "The name should not be blank"
        println(responseEntity.body)
        Assertions.assertTrue(responseEntity.body?.contains(expectedErrorMessage) ?: false)
    }

    @Test
    fun `updateProfile should return HTTP 404 for a non-existent email`() {
        // Make a GET request to the getProfile endpoint with a non-existent email
        val email = "nonexistent@example.com"
        val requestBody = CustomerRequestBody("johndoe@example.com","Mario", "Rossi", "2 Second St","1234567893")
        val credentials = CredentialsLogin("manager", "manager")
        val jwtToken = restTemplate
            .postForEntity("/API/login", credentials, JwtResponse::class.java).body?.jwt ?: ""
        val headers = HttpHeaders()
        headers.setBearerAuth(jwtToken)
        httpEntity = HttpEntity(requestBody, headers)
        val responseEntity = restTemplate.exchange("/API/profiles/${email}", HttpMethod.PUT, httpEntity, String::class.java)

        // Assert that the response has HTTP status 404 (NOT FOUND)
        Assertions.assertEquals(HttpStatus.NOT_FOUND, responseEntity.statusCode)

        // Assert that the response body contains the expected error message
        val expectedErrorMessage = "Customer not found with Email: $email"
        Assertions.assertTrue(responseEntity.body?.contains(expectedErrorMessage) ?: false)
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
    lateinit var httpEntity: HttpEntity<*>
    @Test
    fun `getEmployee should return the employee for a valid id`() {
        val credentials = CredentialsLogin("manager", "manager")
        val jwtToken = restTemplate
            .postForEntity("/API/login", credentials, JwtResponse::class.java).body?.jwt ?: ""
        val headers = HttpHeaders()
        headers.setBearerAuth(jwtToken)
        httpEntity = HttpEntity(null, headers)
        // Create a new customer with a unique email
        val employee = Employee(1,"johndoe@test.it","John","Doe","expert")
        employeeRepository.save(employee)
        // Make a GET request to the getProfile endpoint with the customer's email
        val responseEntity = restTemplate.exchange("/API/employees/${employee.employeeID}", HttpMethod.GET, httpEntity, EmployeeBodyResponse::class.java)

        // Assert that the response has HTTP status 200 (OK)
        Assertions.assertEquals(HttpStatus.OK, responseEntity.statusCode)

        // Assert that the response body is not null
        Assertions.assertNotNull(responseEntity.body)
        // Assert that the response body fields match the customer's data
        Assertions.assertEquals("johndoe@test.it", responseEntity.body?.email)
        Assertions.assertEquals("John", responseEntity.body?.name)
        Assertions.assertEquals("Doe", responseEntity.body?.surname)
        Assertions.assertEquals("expert", responseEntity.body?.role)
    }

    @Test
    fun `getEmployee should return HTTP 404 for a non-existent id`() {
        val credentials = CredentialsLogin("manager", "manager")
        val jwtToken = restTemplate
            .postForEntity("/API/login", credentials, JwtResponse::class.java).body?.jwt ?: ""
        val headers = HttpHeaders()
        headers.setBearerAuth(jwtToken)
        httpEntity = HttpEntity(null, headers)
        // Make a GET request to the getProfile endpoint with a non-existent email
        val id = 54152
        val responseEntity = restTemplate.exchange("/API/employees/$id",HttpMethod.GET, httpEntity, String::class.java)

        // Assert that the response has HTTP status 404 (NOT FOUND)
        Assertions.assertEquals(HttpStatus.NOT_FOUND, responseEntity.statusCode)

        // Assert that the response body contains the expected error message
        val expectedErrorMessage = "Employee not found"
        Assertions.assertTrue(responseEntity.body?.contains(expectedErrorMessage) ?: false)
    }

    @Test
    fun `getEmployee should return HTTP 400 for an invalid id`() {
        val credentials = CredentialsLogin("manager", "manager")
        val jwtToken = restTemplate
            .postForEntity("/API/login", credentials, JwtResponse::class.java).body?.jwt ?: ""
        val headers = HttpHeaders()
        headers.setBearerAuth(jwtToken)
        httpEntity = HttpEntity(null, headers)
        // Make a GET request to the getProfile endpoint with an invalid email
        val invalidId = "notanid"
        val responseEntity = restTemplate.exchange("/API/employees/$invalidId", HttpMethod.GET, httpEntity, String::class.java)

        // Assert that the response has HTTP status 400 (BAD REQUEST)
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.statusCode)

        // Assert that the response body contains the expected error message
        val expectedErrorMessage = "Failed to convert 'id' with value: '$invalidId'"
        println(responseEntity.body)
        Assertions.assertTrue(responseEntity.body?.contains(expectedErrorMessage) ?: false)
    }

    @Test
    fun `addEmployee should add a new employee`() {
        // Create a new customer request body with valid data
        val requestBody = EmployeeBodyRequest("mariorossi@example.com","Mario", "expert", "Rossi")
        val credentials = CredentialsLogin("manager", "manager")
        val jwtToken = restTemplate
            .postForEntity("/API/login", credentials, JwtResponse::class.java).body?.jwt ?: ""
        val headers = HttpHeaders()
        headers.setBearerAuth(jwtToken)
        httpEntity = HttpEntity(requestBody, headers)
        // Make a POST request to the addProfile endpoint with the request body
        val responseEntity = restTemplate.exchange("/API/employee", HttpMethod.POST, httpEntity, EmployeeBodyResponse::class.java)

        // Assert that the response has HTTP status 201 (CREATED)
        Assertions.assertEquals(HttpStatus.CREATED, responseEntity.statusCode)

        // Assert that the response body is not null
        Assertions.assertNotNull(responseEntity.body)

        // Assert that the response body email field matches the request body email field
        Assertions.assertTrue(responseEntity.body?.employeeID!! >0)
        println(responseEntity.body)
        Assertions.assertNull(responseEntity.body?.name)
        Assertions.assertNull(responseEntity.body?.surname)
        Assertions.assertNull(responseEntity.body?.email)
        Assertions.assertNull(responseEntity.body?.role)


        // Assert that the customer was added to the database by checking if it can be retrieved
        val employee = employeeService.getEmployeeByID(responseEntity.body!!.employeeID)
        Assertions.assertNotNull(employee)
        Assertions.assertEquals(requestBody.name, employee?.name)
        Assertions.assertEquals(requestBody.surname, employee?.surname)
        Assertions.assertEquals(requestBody.email, employee?.email)
        Assertions.assertEquals(requestBody.role, employee?.role)
    }

    @Test
    fun `addEmployee should return 400 error for invalid input`() {
        // Create a new customer request body with valid data
        val requestBody = EmployeeBodyRequest("abc","John", "expert", "Doe")
        val credentials = CredentialsLogin("manager", "manager")
        val jwtToken = restTemplate
            .postForEntity("/API/login", credentials, JwtResponse::class.java).body?.jwt ?: ""
        val headers = HttpHeaders()
        headers.setBearerAuth(jwtToken)
        httpEntity = HttpEntity(requestBody, headers)
        // Make a POST request to the addProfile endpoint with the request body
        val responseEntity = restTemplate.exchange("/API/employee", HttpMethod.POST, httpEntity, String::class.java)
        // Assert that the response has HTTP status 201 (CREATED)
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.statusCode)
        val expectedErrorMessage = "The email should be provided in a correct format"
        Assertions.assertTrue(responseEntity.body?.contains(expectedErrorMessage) ?: false)
    }

    @Test
    fun `addEmployee should return 400 error for invalid role`() {
        // Create a new customer request body with valid data
        val requestBody = EmployeeBodyRequest("test@gmail.com","John", "test", "Doe")
        val credentials = CredentialsLogin("manager", "manager")
        val jwtToken = restTemplate
            .postForEntity("/API/login", credentials, JwtResponse::class.java).body?.jwt ?: ""
        val headers = HttpHeaders()
        headers.setBearerAuth(jwtToken)
        httpEntity = HttpEntity(requestBody, headers)
        // Make a POST request to the addProfile endpoint with the request body
        val responseEntity = restTemplate.exchange("/API/employee", HttpMethod.POST, httpEntity, String::class.java)
        // Assert that the response has HTTP status 201 (CREATED)
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.statusCode)
        val expectedErrorMessage = "Role must be expert or manager"
        Assertions.assertTrue(responseEntity.body?.contains(expectedErrorMessage) ?: false)
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
    lateinit var customerRepository: CustomerRepository
    @Autowired
    lateinit var productService: ProductService

    lateinit var httpEntity: HttpEntity<*>
    @Test
    fun `test getAll method should return all products`() {
        val credentials = CredentialsLogin("manager", "manager")
        val jwtToken = restTemplate
            .postForEntity("/API/login", credentials, JwtResponse::class.java).body?.jwt ?: ""
        val headers = HttpHeaders()
        headers.setBearerAuth(jwtToken)
        httpEntity = HttpEntity(null, headers)
        val customer = Customer("johndoe@example.com","John", "Doe", "1234567890", "123 Main St")
        customerRepository.save(customer)
        // Create some test data
        productService.addProduct("1234567890123", "Test Brand 1", "Test Product 1", "johndoe@example.com")
        productService.addProduct("2345678901234", "Test Brand 2", "Test Product 2", "johndoe@example.com")

        // Make a GET request to the /API/products endpoint
        val response = restTemplate.exchange("/API/products", HttpMethod.GET, httpEntity, object : ParameterizedTypeReference<List<ProductResponseBody>>() {})

        // Verify that the response status is OK
        Assertions.assertEquals(HttpStatus.OK, response.statusCode)

        // Verify that the response body contains the expected data
        val responseBody = response.body!!
        Assertions.assertEquals(2, responseBody.size)
        Assertions.assertEquals("1234567890123", responseBody[0].ean)
        Assertions.assertEquals("Test Product 1", responseBody[0].name)
        Assertions.assertEquals("Test Brand 1", responseBody[0].brand)
        Assertions.assertEquals("johndoe@example.com", responseBody[0].customerEmail)
        Assertions.assertEquals("2345678901234", responseBody[1].ean)
        Assertions.assertEquals("Test Product 2", responseBody[1].name)
        Assertions.assertEquals("Test Brand 2", responseBody[1].brand)
        Assertions.assertEquals("johndoe@example.com", responseBody[1].customerEmail)
    }
    @Test
    fun `getProduct should return the product for a valid ean`() {
        val credentials = CredentialsLogin("manager", "manager")
        val jwtToken = restTemplate
            .postForEntity("/API/login", credentials, JwtResponse::class.java).body?.jwt ?: ""
        val headers = HttpHeaders()
        headers.setBearerAuth(jwtToken)
        httpEntity = HttpEntity(null, headers)
        // Create a new customer with a unique email
        val customer = Customer("johndoe@example.com","John", "Doe", "1234567890", "123 Main St")
        customerRepository.save(customer)
        // Create some test data
        productService.addProduct("1234567890123", "Test Brand 1", "Test Product 1", "johndoe@example.com")

        // Make a GET request to the getProfile endpoint with the customer's email
        val responseEntity = restTemplate.exchange("/API/products/1234567890123", HttpMethod.GET, httpEntity, ProductResponseBody::class.java)

        // Assert that the response has HTTP status 200 (OK)
        Assertions.assertEquals(HttpStatus.OK, responseEntity.statusCode)

        // Assert that the response body is not null
        Assertions.assertNotNull(responseEntity.body)

        // Assert that the response body fields match the customer's data
        Assertions.assertEquals("1234567890123", responseEntity.body?.ean)
        Assertions.assertEquals("Test Brand 1", responseEntity.body?.brand)
        Assertions.assertEquals("Test Product 1", responseEntity.body?.name)
        Assertions.assertEquals("johndoe@example.com", responseEntity.body?.customerEmail)
    }

    @Test
    fun `getProduct should return HTTP 404 for a non-existent product`() {
        // Make a GET request to the getProfile endpoint with a non-existent email
        val credentials = CredentialsLogin("manager", "manager")
        val jwtToken = restTemplate
            .postForEntity("/API/login", credentials, JwtResponse::class.java).body?.jwt ?: ""
        val headers = HttpHeaders()
        headers.setBearerAuth(jwtToken)
        httpEntity = HttpEntity(null, headers)
        val ean = "11111111111"
        val responseEntity = restTemplate.exchange("/API/products/$ean", HttpMethod.GET, httpEntity, String::class.java)

        // Assert that the response has HTTP status 404 (NOT FOUND)
        Assertions.assertEquals(HttpStatus.NOT_FOUND, responseEntity.statusCode)

        // Assert that the response body contains the expected error message
        val expectedErrorMessage = "Product not found"
        Assertions.assertTrue(responseEntity.body?.contains(expectedErrorMessage) ?: false)
    }

    @Test
    fun `getProduct should return HTTP 400 for an invalid ean`() {
        val credentials = CredentialsLogin("manager", "manager")
        val jwtToken = restTemplate
            .postForEntity("/API/login", credentials, JwtResponse::class.java).body?.jwt ?: ""
        val headers = HttpHeaders()
        headers.setBearerAuth(jwtToken)
        httpEntity = HttpEntity(null, headers)
        // Make a GET request to the getProfile endpoint with an invalid email
        val invalidEan = "---"
        val responseEntity = restTemplate.exchange("/API/products/$invalidEan", HttpMethod.GET, httpEntity, String::class.java)

        // Assert that the response has HTTP status 400 (BAD REQUEST)
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.statusCode)

        // Assert that the response body contains the expected error message
        val expectedErrorMessage = "The Ean should be alphanumeric"
        println(responseEntity.body)
        Assertions.assertTrue(responseEntity.body?.contains(expectedErrorMessage) ?: false)
    }

    @Test
    fun `addProduct should add a new product`() {

        val customer = Customer("johndoe@example.com","John", "Doe", "1234567890", "123 Main St")
        customerRepository.save(customer)
        // Create a new customer request body with valid data
        val requestBody = ProductRequestBody("1234567890123", "Test Product 1", "Test Brand 1", "johndoe@example.com")
        val credentials = CredentialsLogin("manager", "manager")
        val jwtToken = restTemplate
            .postForEntity("/API/login", credentials, JwtResponse::class.java).body?.jwt ?: ""
        val headers = HttpHeaders()
        headers.setBearerAuth(jwtToken)
        httpEntity = HttpEntity(requestBody, headers)
        // Make a POST request to the addProfile endpoint with the request body
        val responseEntity = restTemplate.exchange("/API/products", HttpMethod.POST, httpEntity, ProductResponseBody::class.java)

        // Assert that the response has HTTP status 201 (CREATED)
        Assertions.assertEquals(HttpStatus.CREATED, responseEntity.statusCode)

        // Assert that the response body is not null
        Assertions.assertNotNull(responseEntity.body)

        // Assert that the response body email field matches the request body email field
        Assertions.assertEquals(requestBody.ean, responseEntity.body?.ean)
        println(responseEntity.body)
        // Assert that the response body other fields are null (as expected)
        Assertions.assertNull(responseEntity.body?.name)
        Assertions.assertNull(responseEntity.body?.customerEmail)
        Assertions.assertNull(responseEntity.body?.brand)

        // Assert that the customer was added to the database by checking if it can be retrieved
        val product = productService.getProduct(requestBody.ean)
        Assertions.assertNotNull(customer)
        Assertions.assertEquals(requestBody.name, product?.name)
        Assertions.assertEquals(requestBody.brand, product?.brand)
        Assertions.assertEquals(requestBody.customerEmail, product?.customer!!.email)
    }
    @Test
    fun `addProduct should return 400 error for invalid input`() {
        // Create a new customer request body with valid data
        val requestBody = ProductRequestBody("£$%", "Test Product 1", "Test Brand 1", "johndoe")
        val credentials = CredentialsLogin("manager", "manager")
        val jwtToken = restTemplate
            .postForEntity("/API/login", credentials, JwtResponse::class.java).body?.jwt ?: ""
        val headers = HttpHeaders()
        headers.setBearerAuth(jwtToken)
        httpEntity = HttpEntity(requestBody, headers)
        // Make a POST request to the addProfile endpoint with the request body
        val responseEntity = restTemplate.exchange("/API/products", HttpMethod.POST, httpEntity, String::class.java)

        // Assert that the response has HTTP status 201 (CREATED)
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.statusCode)
        val expectedErrorMessage = "The email should be provided in a correct format"
        Assertions.assertTrue(responseEntity.body?.contains(expectedErrorMessage) ?: false)
    }

    @Test
    fun `addProduct should return 404 error for customer not found`() {
        // Create a new customer request body with valid data
        val requestBody = ProductRequestBody("123abc", "Test Product 1", "Test Brand 1", "johndoe@abc.it")
        val credentials = CredentialsLogin("manager", "manager")
        val jwtToken = restTemplate
            .postForEntity("/API/login", credentials, JwtResponse::class.java).body?.jwt ?: ""
        val headers = HttpHeaders()
        headers.setBearerAuth(jwtToken)
        httpEntity = HttpEntity(requestBody, headers)
        // Make a POST request to the addProfile endpoint with the request body
        val responseEntity = restTemplate.exchange("/API/products", HttpMethod.POST, httpEntity, String::class.java)

        // Assert that the response has HTTP status 201 (CREATED)
        Assertions.assertEquals(HttpStatus.NOT_FOUND, responseEntity.statusCode)
        val expectedErrorMessage = "Customer not found with Email: johndoe@abc.it"
        Assertions.assertTrue(responseEntity.body?.contains(expectedErrorMessage) ?: false)
    }
}

@Testcontainers
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace=AutoConfigureTestDatabase.Replace.NONE)
class TicketsServerApplicationTests {
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
    lateinit var customerService: CustomerService
    @Autowired
    lateinit var productService: ProductService
    @Autowired
    lateinit var ticketService: TicketService
    @Autowired
    lateinit var employeeService: EmployeeService

    lateinit var httpEntity: HttpEntity<*>

    @Test
    fun `test getTicketHistory method should return the status history of a ticket`() {
        val email = "test@example.com"
        // Create a mock customer and tickets associated with the email
        val customer = CustomerDTO("Test", "Customer", "123456789", "123 Test Street", email)
        customerService.addProfile(customer)
        val product1 = productService.addProduct("1234567890123", "Test Brand 1", "Test Product 1", email)
        val savedTicket1 = ticketService.addTicket("Ticket test1", product1.ean, email)
        val credentials = CredentialsLogin("manager", "manager")
        val jwtToken = restTemplate
            .postForEntity("/API/login", credentials, JwtResponse::class.java).body?.jwt ?: ""
        val headers = HttpHeaders()
        headers.setBearerAuth(jwtToken)
        httpEntity = HttpEntity(null, headers)
        // Make a GET request to the /API/products endpoint
        val response = restTemplate.exchange("/API/ticket/${savedTicket1.ticketID}/history", HttpMethod.GET, httpEntity, object : ParameterizedTypeReference<List<StatusHistoryBodyResponse>>() {})

        // Verify that the response status is OK
        Assertions.assertEquals(HttpStatus.OK, response.statusCode)

        // Verify that the response body contains the expected data
        val responseBody = response.body!!
        println(responseBody)
        Assertions.assertEquals(1, responseBody.size)
        Assertions.assertNotNull(responseBody)
        Assertions.assertNotNull(responseBody[0].statusID)
        Assertions.assertEquals("OPEN", responseBody[0].status)
        Assertions.assertEquals(savedTicket1.ticketID, responseBody[0].ticketID)
        Assertions.assertNotNull(responseBody[0].createdAt)
    }

    @Test
    fun `getTicketHistory should return HTTP 404 for a non-existent ticket`() {
        val id = 0
        val credentials = CredentialsLogin("manager", "manager")
        val jwtToken = restTemplate
            .postForEntity("/API/login", credentials, JwtResponse::class.java).body?.jwt ?: ""
        val headers = HttpHeaders()
        headers.setBearerAuth(jwtToken)
        httpEntity = HttpEntity(null, headers)
        val responseEntity = restTemplate.exchange("/API/ticket/${id}/history", HttpMethod.GET, httpEntity, String::class.java)

        // Assert that the response has HTTP status 404 (NOT FOUND)
        Assertions.assertEquals(HttpStatus.NOT_FOUND, responseEntity.statusCode)

        // Assert that the response body contains the expected error message
        val expectedErrorMessage = "Ticket not found"
        Assertions.assertTrue(responseEntity.body?.contains(expectedErrorMessage) ?: false)
    }

    @Test
    fun `getTicketHistory should return HTTP 400 for an invalid id`() {
        // Make a GET request to the getProfile endpoint with an invalid email
        val invalidId= "---"
        val credentials = CredentialsLogin("manager", "manager")
        val jwtToken = restTemplate
            .postForEntity("/API/login", credentials, JwtResponse::class.java).body?.jwt ?: ""
        val headers = HttpHeaders()
        headers.setBearerAuth(jwtToken)
        httpEntity = HttpEntity(null, headers)
        val responseEntity = restTemplate.exchange("/API/ticket/$invalidId/history", HttpMethod.GET, httpEntity, String::class.java)

        // Assert that the response has HTTP status 400 (BAD REQUEST)
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.statusCode)

        // Assert that the response body contains the expected error message
        val expectedErrorMessage = "Failed to convert 'id' with value: '$invalidId'"
        println(responseEntity.body)
        Assertions.assertTrue(responseEntity.body?.contains(expectedErrorMessage) ?: false)
    }
    @Test
    fun `getTicket should return the ticket for a valid id`() {
        // Create a new customer with a unique email
        val email = "test@example.com"
        // Create a mock customer and tickets associated with the email
        val customer = CustomerDTO("Test", "Customer", "123456789", "123 Test Street", email)
        customerService.addProfile(customer)
        val product1 = productService.addProduct("1234567890123", "Test Brand 1", "Test Product 1", email)
        val savedTicket = ticketService.addTicket("Ticket test1", product1.ean, email)
        val credentials = CredentialsLogin("manager", "manager")
        val jwtToken = restTemplate
            .postForEntity("/API/login", credentials, JwtResponse::class.java).body?.jwt ?: ""
        val headers = HttpHeaders()
        headers.setBearerAuth(jwtToken)
        httpEntity = HttpEntity(null, headers)
        val responseEntity = restTemplate.exchange("/API/ticket/${savedTicket.ticketID}", HttpMethod.GET, httpEntity, TicketBodyResponse::class.java)

        // Assert that the response has HTTP status 200 (OK)
        Assertions.assertEquals(HttpStatus.OK, responseEntity.statusCode)

        // Assert that the response body is not null
        Assertions.assertNotNull(responseEntity.body)
        Assertions.assertTrue(responseEntity.body?.ticketID!! > 0)
        Assertions.assertEquals(savedTicket.description, responseEntity.body?.description)
        Assertions.assertEquals(savedTicket.status,  responseEntity.body?.status)
        Assertions.assertEquals(savedTicket.priority,  responseEntity.body?.priority)
        Assertions.assertEquals(savedTicket.product.ean,  responseEntity.body?.productEan)
        Assertions.assertEquals(savedTicket.customer.email,  responseEntity.body?.customerEmail)
        Assertions.assertEquals(savedTicket.employee?.employeeID,  responseEntity.body?.employeeId)
    }

    @Test
    fun `getTicket should return HTTP 404 for a non-existent ticket`() {
        // Make a GET request to the getProfile endpoint with a non-existent email
        val id = -111
        val credentials = CredentialsLogin("manager", "manager")
        val jwtToken = restTemplate
            .postForEntity("/API/login", credentials, JwtResponse::class.java).body?.jwt ?: ""
        val headers = HttpHeaders()
        headers.setBearerAuth(jwtToken)
        httpEntity = HttpEntity(null, headers)
        val responseEntity = restTemplate.exchange("/API/ticket/$id", HttpMethod.GET, httpEntity, String::class.java)

        // Assert that the response has HTTP status 404 (NOT FOUND)
        Assertions.assertEquals(HttpStatus.NOT_FOUND, responseEntity.statusCode)

        // Assert that the response body contains the expected error message
        val expectedErrorMessage = "Ticket not found"
        Assertions.assertTrue(responseEntity.body?.contains(expectedErrorMessage) ?: false)
    }

    @Test
    fun `getTicket should return HTTP 400 for an invalid id`() {
        // Make a GET request to the getProfile endpoint with an invalid email
        val invalidId = "---"
        val credentials = CredentialsLogin("manager", "manager")
        val jwtToken = restTemplate
            .postForEntity("/API/login", credentials, JwtResponse::class.java).body?.jwt ?: ""
        val headers = HttpHeaders()
        headers.setBearerAuth(jwtToken)
        httpEntity = HttpEntity(null, headers)
        val responseEntity = restTemplate.exchange("/API/ticket/$invalidId", HttpMethod.GET, httpEntity, String::class.java)

        // Assert that the response has HTTP status 400 (BAD REQUEST)
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.statusCode)

        // Assert that the response body contains the expected error message
        val expectedErrorMessage = "Failed to convert 'id' with value: '$invalidId'"
        println(responseEntity.body)
        Assertions.assertTrue(responseEntity.body?.contains(expectedErrorMessage) ?: false)
    }
    @Test
    fun `addTicket should create a new ticket for a valid request`() {
        // Create a new customer with a unique email
        val email = "validemail@example.com"
        val customer = CustomerDTO("Test", "Customer", "123456789", "123 Test Street", email)
        customerService.addProfile(customer)

        // Create a mock product associated with the email
        val product = productService.addProduct("123456", "Test Brand 1", "Test Product 1", email)

        // Create a request body with valid data
        val requestBody =
            TicketBodyRequest(product.ean, "Test Description", email)
        val credentials = CredentialsLogin("manager", "manager")
        val jwtToken = restTemplate
            .postForEntity("/API/login", credentials, JwtResponse::class.java).body?.jwt ?: ""
        val headers = HttpHeaders()
        headers.setBearerAuth(jwtToken)
        httpEntity = HttpEntity(requestBody, headers)
        // Make a POST request to the addTicket endpoint with the request body
        val responseEntity = restTemplate.exchange("/API/ticket", HttpMethod.POST, httpEntity, TicketBodyResponse::class.java)
        println(responseEntity)
        // Assert that the response has HTTP status 201 (CREATED)
        Assertions.assertEquals(HttpStatus.CREATED, responseEntity.statusCode)

        // Assert that the response body is not null
        Assertions.assertNotNull(responseEntity.body)
        Assertions.assertTrue(responseEntity.body?.ticketID!! > 0)
        Assertions.assertEquals(requestBody.description, responseEntity.body?.description)
        Assertions.assertEquals("OPEN", responseEntity.body?.status)
        Assertions.assertNull(responseEntity.body?.priority)
        Assertions.assertNotNull(responseEntity.body?.createdAt)
        Assertions.assertEquals(product.ean, responseEntity.body?.productEan)
        Assertions.assertEquals(email, responseEntity.body?.customerEmail)
        Assertions.assertNull(responseEntity.body?.employeeId)
    }

    @Test
    fun `addTicket should return HTTP 400 for a request with an invalid product ean`() {
        // Create a new customer with a unique email
        val email = "test@example.com"

        // Create a request body with an invalid product ean
        val requestBody = TicketBodyRequest("", "New Ticket", email)
        val credentials = CredentialsLogin("manager", "manager")
        val jwtToken = restTemplate
            .postForEntity("/API/login", credentials, JwtResponse::class.java).body?.jwt ?: ""
        val headers = HttpHeaders()
        headers.setBearerAuth(jwtToken)
        httpEntity = HttpEntity(requestBody, headers)
        // Make a POST request to the addTicket endpoint with the request body
        val responseEntity = restTemplate.exchange("/API/ticket", HttpMethod.POST, httpEntity, String::class.java)

        // Assert that the response has HTTP status 400 (BAD REQUEST)
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.statusCode)

        // Assert that the response body contains the expected error message
        val expectedErrorMessage = "ean"
        Assertions.assertTrue(responseEntity.body?.contains(expectedErrorMessage) ?: false)
    }
    @Test
    fun `addTicket should return 409 error for duplicate ean insertion`() {
        // Create a new customer request body with valid data
        val email = "test@example.com"
        val customer = CustomerDTO("Test", "Customer", "123456789", "123 Test Street", email)
        customerService.addProfile(customer)

        // Create a mock product associated with the email
        val product = productService.addProduct("1234567890123", "Test Brand 1", "Test Product 1", email)
        ticketService.addTicket("Test double ticket", product.ean, email)
        // Create a request body with valid data
        val requestBody =
            TicketBodyRequest(product.ean, "Test Description", email)
        val credentials = CredentialsLogin("manager", "manager")
        val jwtToken = restTemplate
            .postForEntity("/API/login", credentials, JwtResponse::class.java).body?.jwt ?: ""
        val headers = HttpHeaders()
        headers.setBearerAuth(jwtToken)
        httpEntity = HttpEntity(requestBody, headers)
        // Make a POST request to the addTicket endpoint with the request body
        val responseEntity = restTemplate.exchange("/API/ticket", HttpMethod.POST, httpEntity, String::class.java)
        println(responseEntity)
        // Assert that the response has HTTP status 201 (CREATED)
        Assertions.assertEquals(HttpStatus.CONFLICT, responseEntity.statusCode)
        val expectedErrorMessage = "An opened ticket already exists for the ean ${product.ean}"
        Assertions.assertTrue(responseEntity.body?.contains(expectedErrorMessage) ?: false)

    }
    @Test
    fun `addTicket should return 404 error for customer not found`() {
        // Create a new customer request body with valid data
        val email = "test@example.com"
        val customer = CustomerDTO("Test", "Customer", "123456789", "123 Test Street", email)
        customerService.addProfile(customer)

        val customer2 = CustomerDTO("Test", "Customer", "123456789", "123 Test Street", "wrong@test.com")
        customerService.addProfile(customer2)
        // Create a mock product associated with the email
        val product = productService.addProduct("1234567890123", "Test Brand 1", "Test Product 1", email)
        ticketService.addTicket("Test double ticket", product.ean, email)
        // Create a request body with valid data
        val requestBody = TicketBodyRequest(
            product.ean,
            "Test Description",
            "wrong@test.com"
        )
        val credentials = CredentialsLogin("manager", "manager")
        val jwtToken = restTemplate
            .postForEntity("/API/login", credentials, JwtResponse::class.java).body?.jwt ?: ""
        val headers = HttpHeaders()
        headers.setBearerAuth(jwtToken)
        httpEntity = HttpEntity(requestBody, headers)
        // Make a POST request to the addTicket endpoint with the request body
        val responseEntity = restTemplate.exchange("/API/ticket", HttpMethod.POST, httpEntity, String::class.java)
        println(responseEntity)
        // Assert that the response has HTTP status 201 (CREATED)
        Assertions.assertEquals(HttpStatus.NOT_FOUND, responseEntity.statusCode)
        val expectedErrorMessage = "No products for the given customer"
        Assertions.assertTrue(responseEntity.body?.contains(expectedErrorMessage) ?: false)

    }
    @Test
    fun `assignTicket should assign ticket to a valid employee`() {
        // Create a new customer with a unique email
        val email = "test@example.com"
        // Create a mock customer and tickets associated with the email
        val customer = CustomerDTO("Test", "Customer", "123456789", "123 Test Street", email)
        customerService.addProfile(customer)
        val product1 = productService.addProduct("1234567890123", "Test Brand 1", "Test Product 1", email)
        val savedTicket = ticketService.addTicket("Ticket test1", product1.ean, email)

        // Create a new employee
        val employee = employeeService.addEmployee("test@test.it", "Name", "expert", "Surname")

        // Assign the ticket to the employee using the API
        val body = AssignTicketBodyRequest(employee.employeeID!!, "Low")
        val credentials = CredentialsLogin("manager", "manager")
        val jwtToken = restTemplate
            .postForEntity("/API/login", credentials, JwtResponse::class.java).body?.jwt ?: ""
        val headers = HttpHeaders()
        headers.setBearerAuth(jwtToken)
        httpEntity = HttpEntity(body, headers)
        val responseEntity = restTemplate.exchange("/API/ticket/${savedTicket.ticketID}/assign", HttpMethod.PUT, httpEntity, TicketIDBodyResponse::class.java)

        // Assert that the response has HTTP status 200 (OK)
        Assertions.assertEquals(HttpStatus.OK, responseEntity.statusCode)

        // Assert that the response body is not null and contains the updated ticket ID
        Assertions.assertNotNull(responseEntity.body)
        Assertions.assertTrue(responseEntity.body?.ticketID!! > 0)
    }

    @Test
    fun `assignTicket should return HTTP 404 for a non-existent ticket`() {
        // Create a new employee
        val employee = employeeService.addEmployee("test2@test.it", "Name", "expert", "Surname")

        // Assign the ticket to the employee using the API with a non-existent ticket ID
        val body = AssignTicketBodyRequest(employee.employeeID!!, "Low")
        val credentials = CredentialsLogin("manager", "manager")
        val jwtToken = restTemplate
            .postForEntity("/API/login", credentials, JwtResponse::class.java).body?.jwt ?: ""
        val headers = HttpHeaders()
        headers.setBearerAuth(jwtToken)
        httpEntity = HttpEntity(body, headers)
        val responseEntity = restTemplate.exchange("/API/ticket/-111/assign", HttpMethod.PUT, httpEntity, String::class.java)

        // Assert that the response has HTTP status 404 (NOT FOUND)
        Assertions.assertEquals(HttpStatus.NOT_FOUND, responseEntity.statusCode)

        // Assert that the response body contains the expected error message
        val expectedErrorMessage = "Ticket not found"
        Assertions.assertTrue(responseEntity.body?.contains(expectedErrorMessage) ?: false)
    }

    @Test
    fun `assignTicket should return HTTP 400 for an invalid employee id`() {
        // Create a new customer with a unique email
        val email = "test@example.com"
        // Create a mock customer and tickets associated with the email
        val customer = CustomerDTO("Test", "Customer", "123456789", "123 Test Street", email)
        customerService.addProfile(customer)
        val product1 = productService.addProduct("1234567890123", "Test Brand 1", "Test Product 1", email)
        val savedTicket = ticketService.addTicket("Ticket test1", product1.ean, email)
        // Create a mock request body with an invalid employee id
        val body = AssignTicketBodyRequest(-1, "Low")
        val credentials = CredentialsLogin("manager", "manager")
        val jwtToken = restTemplate
            .postForEntity("/API/login", credentials, JwtResponse::class.java).body?.jwt ?: ""
        val headers = HttpHeaders()
        headers.setBearerAuth(jwtToken)
        httpEntity = HttpEntity(body, headers)
        val responseEntity = restTemplate.exchange(
            "/API/ticket/${savedTicket.ticketID}/assign",
            HttpMethod.PUT,
            httpEntity,
            String::class.java
        )

        // Assert that the response has HTTP status 400 (BAD REQUEST)
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.statusCode)
        println(responseEntity)
        // Assert that the response body contains the expected error message
        val expectedErrorMessage = "employeeID"
        Assertions.assertTrue(responseEntity.body?.contains(expectedErrorMessage) ?: false)
    }
    @Test
    fun `editTicketStatus should update the ticket status for a valid id and request body`() {
        // Create a new customer with a unique email
        val email = "test@example.com"
        // Create a mock customer and tickets associated with the email
        val customer = CustomerDTO("Test", "Customer", "123456789", "123 Test Street", email)
        customerService.addProfile(customer)
        val product1 = productService.addProduct("1234567890123", "Test Brand 1", "Test Product 1", email)
        val savedTicket = ticketService.addTicket("Ticket test1", product1.ean, email)

        // Create a request body with a new status for the ticket
        val newStatus = "COMPLETED"
        val requestBody = BodyStatusTicket(newStatus)
        val credentials = CredentialsLogin("manager", "manager")
        val jwtToken = restTemplate
            .postForEntity("/API/login", credentials, JwtResponse::class.java).body?.jwt ?: ""
        val headers = HttpHeaders()
        headers.setBearerAuth(jwtToken)
        httpEntity = HttpEntity(requestBody, headers)
        // Make a PUT request to the editTicketStatus endpoint with the request body
        val response = restTemplate.exchange(
            "/API/ticket/${savedTicket.ticketID}/status",
            HttpMethod.PUT,
            httpEntity,
            Int::class.java
        )

        // Assert that the response has HTTP status 200 (OK)
        Assertions.assertEquals(HttpStatus.OK, response.statusCode)

        // Assert that the response body is not null and contains the updated ticket id
        Assertions.assertNotNull(response.body)
        Assertions.assertEquals(savedTicket.ticketID, response.body)

        // Assert that the ticket status has been updated to the new status
        val updatedTicket = ticketService.getTicket(savedTicket.ticketID!!)
        Assertions.assertEquals(newStatus, updatedTicket?.status)
    }

    @Test
    fun `editTicketStatus should return HTTP 404 for a non-existent ticket`() {
        // Create a request body with a new status for the ticket
        val newStatus = "IN PROGRESS"
        val requestBody = BodyStatusTicket(newStatus)

        // Make a PUT request to the editTicketStatus endpoint with a non-existent ticket id and the request body
        val id = -111
        val credentials = CredentialsLogin("manager", "manager")
        val jwtToken = restTemplate
            .postForEntity("/API/login", credentials, JwtResponse::class.java).body?.jwt ?: ""
        val headers = HttpHeaders()
        headers.setBearerAuth(jwtToken)
        httpEntity = HttpEntity(requestBody, headers)
        val responseEntity = restTemplate.exchange(
            "/API/ticket/$id/status",
            HttpMethod.PUT,
            httpEntity,
            String::class.java
        )

        // Assert that the response has HTTP status 404 (NOT FOUND)
        Assertions.assertEquals(HttpStatus.NOT_FOUND, responseEntity.statusCode)

        // Assert that the response body contains the expected error message
        val expectedErrorMessage = "Ticket not found"
        Assertions.assertTrue(responseEntity.body?.contains(expectedErrorMessage) ?: false)
    }

    @Test
    fun `editTicketStatus should return HTTP 400 for an invalid id`() {
        // Create a request body with a new status for the ticket
        val newStatus = "IN PROGRESS"
        val requestBody = BodyStatusTicket(newStatus)

        // Make a PUT request to the editTicketStatus endpoint with an invalid ticket id and the request body
        val invalidId = "---"
        val credentials = CredentialsLogin("manager", "manager")
        val jwtToken = restTemplate
            .postForEntity("/API/login", credentials, JwtResponse::class.java).body?.jwt ?: ""
        val headers = HttpHeaders()
        headers.setBearerAuth(jwtToken)
        httpEntity = HttpEntity(requestBody, headers)
        val responseEntity = restTemplate.exchange(
            "/API/ticket/$invalidId/status",
            HttpMethod.PUT,
            httpEntity,
            String::class.java
        )

        // Assert that the response has HTTP status 400 (BAD REQUEST)
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.statusCode)

        // Assert that the response body contains the expected error message
        val expectedErrorMessage = "Failed to convert 'id' with value: '$invalidId'"
        Assertions.assertTrue(responseEntity.body?.contains(expectedErrorMessage) ?: false)
    }
}