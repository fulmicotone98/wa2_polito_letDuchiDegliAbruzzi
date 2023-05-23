package wa2.polito.it.letduchidegliabruzzi.server.entity.customer

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import org.jetbrains.annotations.NotNull

data class CustomerDTO (
    @field:NotBlank @field:NotNull val name: String,
    @field:NotBlank @field:NotNull val surname: String,
    val phonenumber: String,
    @field:NotBlank @field:NotNull val address: String,
    @field:Email val email: String
)

fun Customer.toDTO(): CustomerDTO {
    return CustomerDTO(name, surname, phonenumber, address, email )
}

fun CustomerDTO.toCustomer(): Customer {
   return Customer(email, name, surname, phonenumber, address)
}