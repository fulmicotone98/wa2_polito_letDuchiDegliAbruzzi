package wa2.polito.it.letduchidegliabruzzi.server.controller

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
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
import wa2.polito.it.letduchidegliabruzzi.server.dal.dao.chat.ChatService
import wa2.polito.it.letduchidegliabruzzi.server.dal.dao.product.ProductService
import wa2.polito.it.letduchidegliabruzzi.server.dal.dao.ticket.Ticket
import wa2.polito.it.letduchidegliabruzzi.server.dal.dao.ticket.TicketService

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ChatControllerTests {
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
    lateinit var productService: ProductService

    @Autowired
    lateinit var ticketService: TicketService

    @Autowired
    lateinit var userService: UserService

    lateinit var httpEntity: HttpEntity<*>

    @Test
    fun `addChat should create a new chat with valid input`() {
        // Arrange
        val credentials = CredentialsLogin("customer", "customer")
        val jwtToken = restTemplate.postForEntity("/API/login", credentials, JwtResponse::class.java).body?.access_token
            ?: ""
        val headers = HttpHeaders()
        headers.setBearerAuth(jwtToken)
        httpEntity = HttpEntity(null, headers)

        val product = productService.addProduct("1234567890128", "Test Brand 1", "Test Product 1", "customer")
        // Create a new ticket for the customer
        val ticketRequestBody = TicketBodyRequest(product.ean, "Ticket Description")
        httpEntity = HttpEntity(ticketRequestBody, headers)
        val ticketResponseEntity = restTemplate.exchange(
            "/API/ticket",
            HttpMethod.POST,
            httpEntity,
            TicketBodyResponse::class.java
            //String::class.java
        )

        // Extract the created ticket ID
        val ticketId = ticketResponseEntity.body?.ticketID ?: 0
        //val ticketId = ticketResponseEntity.body?.toIntOrNull() ?: 0
        println(ticketResponseEntity)
        // Create a new chat request body
        val chatRequestBody = ChatBodyRequest(ticketId, "Chat Message", emptyList())
        httpEntity = HttpEntity(chatRequestBody, headers)

        // Act
        val responseEntity = restTemplate.exchange(
            "/API/chat",
            HttpMethod.POST,
            httpEntity,
            ChatBodyResponse::class.java
        )

        // Assert
        Assertions.assertEquals(HttpStatus.CREATED, responseEntity.statusCode)
        // Additional assertions based on the expected response for a valid chat creation

    }

    @Test
    fun `addChat should return HTTP 400 for invalid input`() {
        // Arrange
        val credentials = CredentialsLogin("customer", "customer")
        val jwtToken = restTemplate.postForEntity("/API/login", credentials, JwtResponse::class.java).body?.access_token
            ?: ""
        val headers = HttpHeaders()
        headers.setBearerAuth(jwtToken)
        httpEntity = HttpEntity(null, headers)

        // Create a new chat request body with invalid input (e.g., missing required fields)
        val invalidChatRequestBody = ChatBodyRequest(-1, " ", emptyList())
        httpEntity = HttpEntity(invalidChatRequestBody, headers)

        // Act
        val responseEntity = restTemplate.exchange(
            "/API/chat",
            HttpMethod.POST,
            httpEntity,
            String::class.java
        )

        // Assert
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.statusCode)
        // Additional assertions based on the expected response for invalid input
    }
}
