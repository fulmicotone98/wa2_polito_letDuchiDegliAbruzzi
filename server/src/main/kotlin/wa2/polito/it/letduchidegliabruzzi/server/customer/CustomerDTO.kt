package wa2.polito.it.letduchidegliabruzzi.server.customer

data class CustomerDTO (
    val name: String,
    val surname: String,
    val phoneNumber: String,
    val address: String,
    val email: String
)

fun Customer.toDTO(): CustomerDTO {
    return CustomerDTO(email, name, surname, phoneNumber, address )
}