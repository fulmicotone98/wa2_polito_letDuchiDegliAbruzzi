package wa2.polito.it.letduchidegliabruzzi.server.customer

data class CustomerDTO (
    val name: String,
    val surname: String,
    val phonenumber: String,
    val address: String,
    val email: String
)

fun Customer.toDTO(): CustomerDTO {
    return CustomerDTO(email, name, surname, phonenumber, address )
}

fun CustomerDTO.toCustomer(): Customer {
   return Customer(email, name, surname, phonenumber, address)
}