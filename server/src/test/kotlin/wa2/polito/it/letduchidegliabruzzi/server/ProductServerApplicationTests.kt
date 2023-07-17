package wa2.polito.it.letduchidegliabruzzi.server


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