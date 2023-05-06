package wa2.polito.it.letduchidegliabruzzi.server

import junit.framework.TestCase.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import wa2.polito.it.letduchidegliabruzzi.server.customer.*

@Testcontainers
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace=AutoConfigureTestDatabase.Replace.NONE)
class ServerApplicationTests {
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
        assertTrue(responseEntity.body?.contains(expectedErrorMessage) ?: false)
    }

    @Test
    fun `addProfile should add a new customer profile`() {
        // Create a new customer request body with valid data
        val requestBody = CustomerRequestBody("johndoe@example.com","John", "Doe", "123 Main St","1234567890")

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
}