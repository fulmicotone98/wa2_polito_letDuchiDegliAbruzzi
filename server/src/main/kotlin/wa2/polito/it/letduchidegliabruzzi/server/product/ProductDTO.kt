package wa2.polito.it.letduchidegliabruzzi.server.product

import wa2.polito.it.letduchidegliabruzzi.server.customer.Customer

data class ProductDTO(
    val ean: String,
    val name: String,
    val brand: String,
    val customer: Customer?
)

fun Product.toDTO(): ProductDTO {
    return ProductDTO(ean, name, brand, customer)
}