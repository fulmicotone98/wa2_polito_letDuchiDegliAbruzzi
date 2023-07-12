package wa2.polito.it.letduchidegliabruzzi.server.dal.dao.product

import wa2.polito.it.letduchidegliabruzzi.server.dal.authDao.UserDTO

data class ProductDTO(
    val ean: String,
    val name: String,
    val brand: String,
    val customer: UserDTO
)

fun Product.toDTO(customer: UserDTO): ProductDTO {
    return ProductDTO(ean, name, brand, customer)
}

fun ProductDTO.toProduct(): Product {
    return Product(ean, name, brand, customer.username)
}