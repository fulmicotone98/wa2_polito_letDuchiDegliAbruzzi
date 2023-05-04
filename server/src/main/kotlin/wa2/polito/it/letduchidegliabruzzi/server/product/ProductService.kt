package wa2.polito.it.letduchidegliabruzzi.server.product

interface ProductService {
    fun getAll(): List<ProductDTO>
    fun getProduct(ean: String): ProductDTO?
    fun addProduct(ean: String, brand: String, name: String, customer_email: String?): Product
}