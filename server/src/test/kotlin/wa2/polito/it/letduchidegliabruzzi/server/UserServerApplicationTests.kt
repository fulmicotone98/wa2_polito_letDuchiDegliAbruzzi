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
import wa2.polito.it.letduchidegliabruzzi.server.dal.authDao.UserDTO
import wa2.polito.it.letduchidegliabruzzi.server.dal.authDao.UserService
import wa2.polito.it.letduchidegliabruzzi.server.dal.dao.product.ProductService
import wa2.polito.it.letduchidegliabruzzi.server.dal.dao.ticket.TicketService

@Testcontainers
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
class UserServerApplicationTests {
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
    lateinit var userService: UserService
    @Autowired
    lateinit var productService: ProductService
    @Autowired
    lateinit var ticketService: TicketService

    lateinit var httpEntity: HttpEntity<*>

    @Test
    fun `getProfile should return the customer profile for a valid username`() {

        val credentials = CredentialsLogin("manager", "manager")
        val jwtToken = restTemplate
            .postForEntity("/API/login", credentials, JwtResponse::class.java).body?.access_token ?: ""
        val headers = HttpHeaders()
        headers.setBearerAuth(jwtToken)
        httpEntity = HttpEntity(null, headers)

        // Create a new customer with a unique username
        val customer = UserBody("johndoe","johndoe@example.com","password","John", "Doe", "1234567890", "123 Main St")
        userService.addUser(customer, listOf("Customers_group"))

        // Make a GET request to the getProfile endpoint with the customer's username
        val responseEntity = restTemplate.exchange(
            "/API/user/${customer.username}",
            HttpMethod.GET,
            httpEntity,
            UserDTO::class.java
        )

        // Assert that the response has HTTP status 200 (OK)
        Assertions.assertEquals(HttpStatus.OK, responseEntity.statusCode)

        // Assert that the response body is not null
        Assertions.assertNotNull(responseEntity.body)

        // Assert that the response body fields match the customer's data
        Assertions.assertEquals(customer.emailID, responseEntity.body?.email)
        Assertions.assertEquals(customer.username, responseEntity.body?.username)
        Assertions.assertEquals(customer.firstName, responseEntity.body?.name)
        Assertions.assertEquals(customer.lastName, responseEntity.body?.surname)
        Assertions.assertEquals(customer.phoneNumber, responseEntity.body?.phonenumber)
        Assertions.assertEquals(customer.address, responseEntity.body?.address)
    }

    @Test
    fun `getProfile should return HTTP 404 for a non-existent username`() {

        val credentials = CredentialsLogin("manager", "manager")
        val jwtToken = restTemplate
            .postForEntity("/API/login", credentials, JwtResponse::class.java).body?.access_token ?: ""
        val headers = HttpHeaders()
        headers.setBearerAuth(jwtToken)
        httpEntity = HttpEntity(null, headers)

        // Make a GET request to the getProfile endpoint with a non-existent email
        val username = "nonexistent"
        val responseEntity = restTemplate.exchange("/API/user/$username", HttpMethod.GET, httpEntity, String::class.java)

        // Assert that the response has HTTP status 404 (NOT FOUND)
        Assertions.assertEquals(HttpStatus.NOT_FOUND, responseEntity.statusCode)

        // Assert that the response body contains the expected error message
        val expectedErrorMessage = "Customer not found with username: $username"
        Assertions.assertTrue(responseEntity.body?.contains(expectedErrorMessage) ?: false)
    }

    @Test
    fun `getProfile should return HTTP 400 for an invalid username`() {
        // Make a GET request to the getProfile endpoint with an invalid username
        val invalidUsername = " "
        val credentials = CredentialsLogin("manager", "manager")
        val jwtToken = restTemplate
            .postForEntity("/API/login", credentials, JwtResponse::class.java).body?.access_token ?: ""
        val headers = HttpHeaders()
        headers.setBearerAuth(jwtToken)
        httpEntity = HttpEntity(null, headers)
        val responseEntity = restTemplate.exchange("/API/user/${invalidUsername}", HttpMethod.GET, httpEntity, String::class.java)
        println(responseEntity.body)
        val expectedErrorMessage = "Username shouldn't be blank"
        Assertions.assertTrue(responseEntity.body?.contains(expectedErrorMessage) ?: false)
        // Assert that the response has HTTP status 400 (BAD REQUEST)
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.statusCode)
    }
    @Test
    fun `updateProfile should return 400 error for invalid input`() {
        val requestBody = CustomerRequestBody("johndoe","manager","John", "Rossi", "2 Second St","1234567893")
        // Make a PUT request to the updateProfile endpoint with the request body
        val credentials = CredentialsLogin("manager", "manager")
        val jwtToken = restTemplate
            .postForEntity("/API/login", credentials, JwtResponse::class.java).body?.access_token ?: ""
        val headers = HttpHeaders()
        headers.setBearerAuth(jwtToken)
        httpEntity = HttpEntity(requestBody, headers)
        val responseEntity = restTemplate.exchange("/API/user", HttpMethod.PUT, httpEntity, String::class.java)
        println(responseEntity)
        // Assert that the response has HTTP status 204 (NO_CONTENT)
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.statusCode)
    }

    @Test
    fun `getUserInfo should return the user's information for a valid authentication token`() {


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
            "/API/userinfo",
            HttpMethod.GET,
            httpEntity,
            UserDTO::class.java
        )

        // Assert
        Assertions.assertEquals(HttpStatus.OK, responseEntity.statusCode)
    }

    @Test
    fun `getUserInfo should return HTTP 401 for an invalid or missing authentication token`() {
        val headers = HttpHeaders()
        httpEntity = HttpEntity(null, headers)
        // Act
        val responseEntity = restTemplate.exchange(
            "/API/userinfo",
            HttpMethod.GET,
            httpEntity,
            String::class.java
        )

        // Assert
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.statusCode)
    }

    @Test
    fun `getExperts should return a list of expert users`() {
        val credentials = CredentialsLogin("manager", "manager")
        val jwtToken = restTemplate.postForEntity(
            "/API/login",
            credentials,
            JwtResponse::class.java
        ).body?.access_token ?: ""
        val headers = HttpHeaders()
        headers.setBearerAuth(jwtToken)
        httpEntity = HttpEntity(null, headers)
        val expert1 = UserBody("test1","test1@example.com","password","John", "Doe", "1234567890", "123 Main St")
        userService.addUser(expert1, listOf("Experts_group"))
        val expert2 = UserBody("test2","test2@example.com","password","John", "Doe", "1234567890", "123 Main St")
        userService.addUser(expert2, listOf("Experts_group"))
        val expert1DTO = UserDTO(null,expert1.username,expert1.emailID,expert1.firstName,expert1.lastName,expert1.phoneNumber,expert1.address, null)
        val expert2DTO = UserDTO(null,expert2.username,expert2.emailID,expert2.firstName,expert2.lastName,expert2.phoneNumber,expert2.address, null)
        // Act
        val responseEntity = restTemplate.exchange(
            "/API/users/experts",
            HttpMethod.GET,
            httpEntity,
            object : ParameterizedTypeReference<List<UserDTO>>() {}
        )

        // Assert
        Assertions.assertEquals(HttpStatus.OK, responseEntity.statusCode)
        println(responseEntity.body)
        Assertions.assertTrue(responseEntity.body!!.map { x -> x.username }.contains(expert1DTO.username))
        Assertions.assertTrue(responseEntity.body!!.map { x -> x.username }.contains(expert2DTO.username))
        userService.deleteUserByUsername(expert1.username)
        userService.deleteUserByUsername(expert2.username)
    }

    @Test
    fun `getExperts should return HTTP 401 for an invalid or missing authentication token`() {
        val headers = HttpHeaders()
        httpEntity = HttpEntity(null, headers)
        // Act
        val responseEntity = restTemplate.exchange(
            "/API/users/experts",
            HttpMethod.GET,
            httpEntity,
            String::class.java
        )

        // Assert
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.statusCode)
    }
    @Test
    fun `createExpert should create a new expert with valid user body`() {

        val credentials = CredentialsLogin("manager", "manager")
        val jwtToken = restTemplate
            .postForEntity("/API/login", credentials, JwtResponse::class.java).body?.access_token ?: ""
        val headers = HttpHeaders()
        headers.setBearerAuth(jwtToken)
        // Arrange
        val userBody = UserBody("test", "test@example.com", "password", "John", "Doe", "1234567890", "123 Main St")

        httpEntity = HttpEntity(userBody, headers)
        // Act
        val responseEntity = restTemplate.exchange(
            "/API/user/createExpert",
            HttpMethod.POST,
            httpEntity,
            UserBody::class.java
        )

        // Assert
        Assertions.assertEquals(HttpStatus.CREATED, responseEntity.statusCode)
        Assertions.assertEquals(userBody, responseEntity.body)
        userService.deleteUserByUsername(userBody.username)
    }

    @Test
    fun `createExpert should return HTTP 400 for invalid user body`() {
        val credentials = CredentialsLogin("manager", "manager")
        val jwtToken = restTemplate
            .postForEntity("/API/login", credentials, JwtResponse::class.java).body?.access_token ?: ""
        val headers = HttpHeaders()
        headers.setBearerAuth(jwtToken)
        // Arrange
        val userBody = UserBody("test", "test", "password", "John", "Doe", "1234567890", "123 Main St")

        httpEntity = HttpEntity(userBody, headers)
        // Act
        val responseEntity = restTemplate.exchange(
            "/API/user/createExpert",
            HttpMethod.POST,
            httpEntity,
            String::class.java
        )
        println(responseEntity)
        // Assert
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.statusCode)
    }

    @Test
    fun `createExpert should return HTTP 409 for double user`() {
        val credentials = CredentialsLogin("manager", "manager")
        val jwtToken = restTemplate
            .postForEntity("/API/login", credentials, JwtResponse::class.java).body?.access_token ?: ""
        val headers = HttpHeaders()
        headers.setBearerAuth(jwtToken)
        // Arrange
        val userBody = UserBody("test", "test@test.it", "password", "John", "Doe", "1234567890", "123 Main St")

        httpEntity = HttpEntity(userBody, headers)
        // Act
        var responseEntity = restTemplate.exchange(
            "/API/user/createExpert",
            HttpMethod.POST,
            httpEntity,
            String::class.java
        )
        val userBody2 = UserBody("test", "test@test.it", "password", "John", "Doe", "1234567890", "123 Main St")

        httpEntity = HttpEntity(userBody2, headers)
        // Act
        responseEntity = restTemplate.exchange(
            "/API/user/createExpert",
            HttpMethod.POST,
            httpEntity,
            String::class.java
        )
        println(responseEntity)
        // Assert
        Assertions.assertEquals(HttpStatus.CONFLICT, responseEntity.statusCode)
        userService.deleteUserByUsername(userBody.username)
    }
    /*@Test
    fun `updateProfile should update an existing customer profile`() {
        // Create a new customer request body with valid data
        val customer = UserBody("johndoe","johndoe@example.com","password","John", "Doe", "1234567890","123 Main St")
        userService.addUser(customer,listOf("Customers_group"))
        val requestBody = CustomerRequestBody("johndoe@example.com","mariorossi","Mario", "Rossi", "2 Second St","1234567893")

        val credentials = CredentialsLogin("manager", "manager")
        val jwtToken = restTemplate
            .postForEntity("/API/login", credentials, JwtResponse::class.java).body?.access_token ?: ""
        val headers = HttpHeaders()
        headers.setBearerAuth(jwtToken)
        httpEntity = HttpEntity(requestBody, headers)// Make a PUT request to the updateProfile endpoint with the request body
        val responseEntity = restTemplate.exchange("/API/profiles/${customer.username}", HttpMethod.PUT, httpEntity, String::class.java)
        println(responseEntity)
        // Assert that the response has HTTP status 204 (NO_CONTENT)
        Assertions.assertEquals(HttpStatus.NO_CONTENT, responseEntity.statusCode)

        // Assert that the customer was added to the database by checking if it can be retrieved
        val newCustomer = userService.getUserByUsername(requestBody.username)
        Assertions.assertNotNull(customer)
        Assertions.assertEquals(requestBody.email, newCustomer?.email)
        Assertions.assertEquals(requestBody.name, newCustomer?.name)
        Assertions.assertEquals(requestBody.surname, newCustomer?.surname)
        Assertions.assertEquals(requestBody.phonenumber, newCustomer?.phonenumber)
        Assertions.assertEquals(requestBody.address, newCustomer?.address)
    }

    */
}