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
    val customer = Customer()
    customer.email = email
    customer.phonenumber = phonenumber
    customer.surname = surname
    customer.address = address
    customer.name = name
    return customer
}