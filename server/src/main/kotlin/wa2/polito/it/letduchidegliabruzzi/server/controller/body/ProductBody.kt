package wa2.polito.it.letduchidegliabruzzi.server.controller.body

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern

data class ProductRequestBody(
    @field:NotBlank @Pattern(regexp = "^[A-Za-z0-9]+\$", message = "The Ean should be alphanumeric") val ean: String,
    @field:NotBlank val name: String,
    @field:NotBlank val brand: String
)

data class ProductResponseBody(
    @field:NotBlank @field:NotNull val ean: String,
    @field:NotBlank @field:NotNull val name: String,
    @field:NotBlank @field:NotNull val brand: String,
    @field:NotBlank @field:NotNull val customerUsername: String
)

data class ProductBodyID(val ean: String)