package wa2.polito.it.letduchidegliabruzzi.server.controller.body

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class CustomerResponseBody(
    @field:NotNull @field:NotBlank @field:Email val email: String?,
    @field:NotNull @field:NotBlank val name: String?,
    @field:NotNull @field:NotBlank val surname: String?,
    @field:NotNull @field:NotBlank val address: String?,
    val phonenumber: String?
)

data class CustomerRequestBody(
    @field:NotNull @field:NotBlank @field:Email(message = "The email should be provided in a correct format") val email: String = "",
    @field:NotNull @field:NotBlank val username: String = "",
    @field:NotNull @field:NotBlank(message = "The name should not be blank") val name: String= "",
    @field:NotNull @field:NotBlank(message = "The surname should not be blank") val surname: String = "",
    @field:NotNull @field:NotBlank(message = "The address should not be blank") val address: String = "",
    val phonenumber: String = ""
)