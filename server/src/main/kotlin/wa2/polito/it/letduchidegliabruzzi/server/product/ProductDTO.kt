package wa2.polito.it.letduchidegliabruzzi.server.product

data class ProductDTO(
    val ean: String,
    val name: String,
    val brand: String,
    val customerEmail: Int
)

fun Product.toDTO(): ProductDTO {
    return ProductDTO(ean, name, brand, customerEmail)
}