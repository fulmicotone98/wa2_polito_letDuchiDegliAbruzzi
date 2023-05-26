package wa2.polito.it.letduchidegliabruzzi.server.controller.body

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive

data class EmployeeBodyRequest(
    @field:NotBlank @field:Email(message ="The email should be provided in a correct format") val email: String,
    @field:NotBlank val name: String,
    @field:NotBlank val role: String,
    @field:NotBlank val surname: String
)

data class EmployeeBodyResponse(
    @field:Positive @field:NotNull val employeeID: Int = 0,
    @field:NotBlank @field:NotNull @field:Email val email: String? = "",
    @field:NotBlank @field:NotNull val name: String? = "",
    @field:NotBlank @field:NotNull val role: String? = "",
    @field:NotBlank @field:NotNull val surname: String? = ""
)