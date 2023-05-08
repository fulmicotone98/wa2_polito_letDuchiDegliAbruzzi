package wa2.polito.it.letduchidegliabruzzi.server.product

interface ProductService {
    fun getAll(): List<ProductDTO>
    fun getProduct(ean: String): ProductDTO?
}