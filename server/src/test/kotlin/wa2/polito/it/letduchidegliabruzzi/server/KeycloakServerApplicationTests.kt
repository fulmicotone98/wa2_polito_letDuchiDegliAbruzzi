package wa2.polito.it.letduchidegliabruzzi.server

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.*
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import wa2.polito.it.letduchidegliabruzzi.server.controller.body.CredentialsLogin
import wa2.polito.it.letduchidegliabruzzi.server.controller.body.JwtResponse
import wa2.polito.it.letduchidegliabruzzi.server.controller.body.KeycloakResponse
import wa2.polito.it.letduchidegliabruzzi.server.controller.body.UserBody
import wa2.polito.it.letduchidegliabruzzi.server.dal.authDao.UserService

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class KeycloakControllerTests {
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

    lateinit var httpEntity: HttpEntity<*>

    @Test
    fun `login should return JWT token for valid credentials`() {
        // Arrange
        val credentials = CredentialsLogin("manager", "manager")
        val responseEntity = restTemplate.postForEntity(
            "/API/login",
            credentials,
            JwtResponse::class.java
        )//.body?.access_token ?: ""

        // Assert
        Assertions.assertEquals(HttpStatus.OK, responseEntity.statusCode)
        Assertions.assertNotNull(responseEntity.body)
        Assertions.assertTrue(responseEntity.body?.access_token != "")
    }

    @Test
    fun `login should return HTTP 401 for invalid credentials`() {
        // Arrange
        val credentials = CredentialsLogin("username", "password")

        // Act
        val responseEntity = restTemplate.postForEntity(
            "/API/login",
            credentials,
            String::class.java
        )

        // Assert
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.statusCode)
    }
    @Test
    fun `signup should work for valid input`() {
        val user = UserBody("prova", "prova@test.it","password","Prova","Test","1234578279","Via Roma,10")
        val headers = HttpHeaders()
        httpEntity = HttpEntity(user, headers)
        // Act
        val responseEntity = restTemplate.exchange(
            "/API/signup",
            HttpMethod.POST,
            httpEntity,
            Any::class.java
        )

        Assertions.assertEquals(HttpStatus.CREATED, responseEntity.statusCode)
        userService.deleteUserByUsername(user.username)
    }

    @Test
    fun `logout should work for valid credentials`() {
        // Arrange
        val credentials = CredentialsLogin("manager", "manager")
        val jwtResponse = restTemplate
            .postForEntity("/API/login", credentials, KeycloakResponse::class.java).body
        val headers = HttpHeaders()
        headers.setBearerAuth(jwtResponse?.accessToken!!)
        headers.contentType = MediaType.APPLICATION_JSON
        println(jwtResponse)
        httpEntity = HttpEntity(jwtResponse, headers)

        // Act
        val responseEntity = restTemplate.exchange(
            "/API/logout",
            HttpMethod.POST,
            httpEntity,
            Any::class.java
        )

        Assertions.assertEquals(HttpStatus.NO_CONTENT, responseEntity.statusCode)
    }


}