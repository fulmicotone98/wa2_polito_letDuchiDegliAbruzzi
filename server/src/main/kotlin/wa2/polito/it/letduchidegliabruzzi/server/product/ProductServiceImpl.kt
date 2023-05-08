package wa2.polito.it.letduchidegliabruzzi.server.product

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import wa2.polito.it.letduchidegliabruzzi.server.customer.Customer
import wa2.polito.it.letduchidegliabruzzi.server.customer.CustomerRepository

@Service
class ProductServiceImpl(
    private val productRepository: ProductRepository,
    private val customerRepository: CustomerRepository) : ProductService {
        @Transactional(readOnly = true, isolation = Isolation.SERIALIZABLE)
        override fun getAll(): List<ProductDTO> {
            return productRepository.findAll().map { it.toDTO() }
        }

        @Transactional(readOnly = true, isolation = Isolation.SERIALIZABLE)
        override fun getProduct(ean: String): ProductDTO? {
            val product = productRepository.findByIdOrNull(ean)
                ?: throw ProductNotFoundException("Product not found with EAN: $ean")
            return product.toDTO()
        }

        @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
        override fun addProduct(ean: String, brand: String, name: String, customerEmail: String): Product {
            val customer: Customer?
            customer = customerRepository.getReferenceById(customerEmail)
            val newProductDTO = ProductDTO(ean,name,brand, customer)
            return productRepository.save(newProductDTO.toProduct())
        }
}